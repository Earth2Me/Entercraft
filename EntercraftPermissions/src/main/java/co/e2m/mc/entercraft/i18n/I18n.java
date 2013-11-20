package co.e2m.mc.entercraft.i18n;

import co.e2m.mc.entercraft.api.IComponentsPlugin;
import co.e2m.mc.entercraft.api.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;


/**
 * Internationalization manager.
 */
// TODO: Specify file from which to load localization strings
public final class I18n extends Component
{
	/**
	 * Used as the locale for identifiers and other locale-independent strings.
	 */
	public static final Locale INVARIANT_LOCALE = Locale.ROOT;

	private static final Map<Formats, String> formats = new EnumMap<>(Formats.class);
	private static final Pattern tokenizeRegex = Pattern.compile("\\s+:");
	private static final Matcher craftizeMatcher = Pattern.compile("&([^&])").matcher("");

	@Getter
	private static I18n instance;

	/**
	 * Instantiates a new internationalization manager.
	 *
	 * @param plugin the parent plugin from which some information may be acquired, such as logger
	 */
	public I18n(final IComponentsPlugin plugin)
	{
		super(plugin);

		loadDefaults();
	}

	/**
	 * Sets the primary instance.
	 *
	 * @param instance the instance to mark as the primary instance
	 * @return the primary instance, for easy chaining
	 */
	public static I18n setPrimary(final I18n instance)
	{
		I18n.instance = instance;
		return instance;
	}

	/**
	 * Marks this instance as the primary internationalization manager, which will be called by static method.
	 *
	 * @return self, for easy chaining
	 */
	public I18n makePrimary()
	{
		return setPrimary(instance);
	}

	/**
	 * Determines whether the current instance is the primary internationalization manager.
	 *
	 * @return true if the current instance is the primary internationalization manager; otherwise, false
	 */
	public boolean isPrimary()
	{
		return equals(instance);
	}

	/**
	 * Applies arguments to a localized format.
	 *
	 * These formats are run through String.format after being localized.
	 *
	 * @param key the localized format to use
	 * @param args arguments for the localized format
	 * @return a localized, formatted string
	 */
	// Synchronize documentation below.
	public String format(final Formats key, final Object... args)
	{
		if (formats.containsKey(key))
		{
			return String.format(formats.get(key), args);
		}
		else
		{
			return String.format(craftize(key.getFallback()), args) + "  [Locale Entry Missing]";
		}
	}

	/**
	 * Applies arguments to a localized format.
	 *
	 * These formats are run through String.format after being localized.
	 *
	 * @param key the localized format to use
	 * @param args arguments for the localized format
	 * @return a localized, formatted string
	 */
	// Synchronize documentation above.
	public static String i(final Formats key, final Object... args)
	{
		if (instance == null)
		{
			throw new IllegalStateException("There is no internationalization manager (I18n) instance available.");
		}

		return instance.format(key, args);
	}

	/**
	 * Formats a string for Minecraft output by replacing ampersand-markup with formatting codes.
	 *
	 * Ampersands (&) will generally replaced with section markers (§). And ampersand occurring at the end of a string will not be replaced.
	 * Double-up ampersands to escape them: && will be replaced with &.
	 *
	 * @param markup ampersand-markup string to be formatted
	 * @return a Minecraft-compatible formatted string
	 */
	public String craftize(final String markup)
	{
		return craftizeMatcher.reset(markup).replaceAll("§$1").intern();
	}

	/**
	 * Load the default fallback formats.
	 */
	public void loadDefaults()
	{
		for (final Formats format : Formats.values())
		{
			formats.put(format, craftize(format.getFallback()));
		}
	}

	/**
	 * Load locale from file or defaults from code.
	 *
	 * @param localeFile the file to load, or null for defaults
	 * @return true if (partially) successful; otherwise, false
	 */
	public boolean load(final File localeFile)
	{
		if (localeFile == null)
		{
			loadDefaults();
			return true;
		}

		try (
				final FileInputStream inputStream = new FileInputStream(localeFile);
				final InputStreamReader inputReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
				final BufferedReader rx = new BufferedReader(inputReader);
			)
		{
			boolean isSuccess = false;

			for (String line; (line = rx.readLine()) != null;)
			{
				if (loadLine(line))
				{
					isSuccess = true;
				}
			}

			return isSuccess;
		}
		catch (final FileNotFoundException ex)
		{
			getLogger().log(Level.WARNING, i(Formats.Error_Locale_FileNonExistent, localeFile), ex);
			return false;
		}
		catch (final IOException ex)
		{
			getLogger().log(Level.WARNING, i(Formats.Error_Generic_CannotOpenFile, localeFile), ex);
			return false;
		}
	}

	/**
	 * Attempts to load a single line of locale data, typically from a file.
	 *
	 * @param line the line of locale data from a text file
	 * @return true if successful; otherwise, false
	 */
	private boolean loadLine(final String line)
	{
		if (line.isEmpty() || line.startsWith("#"))
		{
			return false;
		}

		final String[] tokens = tokenizeRegex.split(line, 2);
		if (tokens.length < 2)
		{
			return false;
		}
		final String node = tokens[0];
		final String formatText = tokens[1];

		final Formats formatKey;
		try
		{
			formatKey = Formats.valueOf(node.replace(Formats.NODE_SEPARATOR, '_'));
		}
		catch (final IllegalArgumentException ex)
		{
			getLogger().log(Level.WARNING, i(Formats.Error_Locale_NodeInvalid, node), ex);
			return false;
		}

		formats.put(formatKey, formatText);
		return true;
	}
}

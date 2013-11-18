package com.earth2me.mc.cloudpermissions.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.bukkit.plugin.Plugin;


/**
 * Internationalization manager.
 */
public final class I18n
{
	private final static Map<Formats, String> formats = new EnumMap<>(Formats.class);
	private final static Pattern tokenizeRegex = Pattern.compile("\\s+:");
	private final static Matcher craftizeMatcher = Pattern.compile("&([^&])").matcher("");

	@Getter
	private static I18n instance;

	@SuppressWarnings("NonConstantLogger")
	private final Logger logger;

	/**
	 * Instantiates a new internationalization manager.
	 *
	 * @param plugin the parent plugin from which some information may be acquired, such as logger
	 */
	public I18n(Plugin plugin)
	{
		this.logger = plugin.getLogger();

		load(null);
	}

	/**
	 * Marks this instance as the primary internationalization manager, which will be called by static method.
	 *
	 * @return self, for easy chaining/assignment
	 */
	public I18n makePrimary()
	{
		instance = this;
		return this;
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
	public static String _(final Formats key, final Object... args)
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
	 * Load locale from file or defaults from code.
	 *
	 * @param localeFile the file to load, or null for defaults
	 * @return true if (partially) successful; otherwise, false
	 */
	public boolean load(final File localeFile)
	{
		boolean isSuccess = false;

		if (localeFile == null)
		{
			// This could be an initial load, so we we can't throw localized exceptions.

			for (final Formats format : Formats.values())
			{
				formats.put(format, craftize(format.getFallback()));
				isSuccess = true;
			}
		}
		else
		{
			// We've at least initialized with fallback formats, so it's safe to use localized exceptions.

			if (!localeFile.exists())
			{
				_(Formats.Error_Locale_FileNonExistent, localeFile);
				return false;
			}

			try (
					final FileReader fileReader = new FileReader(localeFile);
					final BufferedReader reader = new BufferedReader(fileReader);)
			{

				for (String line; (line = reader.readLine()) != null;)
				{
					if (line.isEmpty() || line.startsWith("#"))
					{
						continue;
					}

					final String[] tokens = tokenizeRegex.split(line, 2);
					if (tokens.length < 2)
					{
						continue;
					}
					final String node = tokens[0];
					final String formatText = tokens[1];

					final Formats formatKey;
					try
					{
						formatKey = Formats.valueOf(node.replace(Formats.NODE_SEPARATOR, '_'));
					}
					catch (IllegalArgumentException ex)
					{
						logger.log(Level.WARNING, _(Formats.Error_Locale_NodeInvalid, node), ex);
						continue;
					}

					formats.put(formatKey, formatText);
					isSuccess = true;
				}
			}
			catch (final FileNotFoundException ex)
			{
				logger.log(Level.WARNING, _(Formats.Error_Locale_FileNonExistent, localeFile), ex);
				return false;
			}
			catch (final IOException ex)
			{
				logger.log(Level.WARNING, _(Formats.Error_Generic_CannotOpenFile, localeFile), ex);
				return false;
			}
		}

		return isSuccess;
	}
}

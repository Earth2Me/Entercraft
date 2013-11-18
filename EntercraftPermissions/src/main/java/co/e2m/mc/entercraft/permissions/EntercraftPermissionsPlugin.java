package co.e2m.mc.entercraft.permissions;

import co.e2m.mc.entercraft.permissions.commands.CommandWrapper;
import co.e2m.mc.entercraft.permissions.i18n.Formats;
import co.e2m.mc.entercraft.permissions.i18n.I18n;
import static co.e2m.mc.entercraft.permissions.i18n.I18n.i;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Provides the main entrypoint and manager for the Bukkit plugin.
 */
public final class EntercraftPermissionsPlugin extends JavaPlugin
{
	/**
	 * Collection of all tracked plugin components.
	 */
	private final transient List<IComponent> components = new ArrayList<>();

	/**
	 * Gets the internationalization component.
	 *
	 * @return the internationalization component
	 */
	@Getter
	/**
	 * The internationalization component.
	 */
	private transient I18n i18n;

	/**
	 * Gets the command wrapper component.  The command wrapper delegates subcommand handling.
	 *
	 * @return the command wrapper component
	 */
	@Getter
	/**
	 * The command wrapper component.
	 */
	private transient CommandWrapper commandWrapper;

	/**
	 * Used to prevent more than one call to onLoad.
	 */
	private transient boolean isLoaded = false;

	/**
	 * Loads all core components. Invoked when the plugin first loads.
	 */
	private void loadComponents()
	{
		i18n = addComponent(new I18n(this));
		commandWrapper = addComponent(new CommandWrapper(this));
	}

	/**
	 * Gets a list of all tracked plugin components.
	 *
	 * @return an unmodifiable list of all tracked plugin components
	 */
	public List<IComponent> getComponents()
	{
		return Collections.unmodifiableList(components);
	}

	/**
	 * Attempts to load a component. If successful, the component is tracked.
	 *
	 * If the plugin has already been enabled, the component will be enabled immediately; otherwise, the component will be enabled when the
	 * plugin is enabled.
	 *
	 * @param <T> the type of the component
	 * @param component an instantiated component to load
	 * @return the component if successful; otherwise, null
	 */
	public <T extends IComponent> T addComponent(final T component)
	{
		try
		{
			component.onLoad();

			if (isEnabled())
			{
				component.onEnable();
			}

			components.add(component);
			return component;
		}
		catch (Throwable ex)
		{
			getLogger().log(Level.SEVERE, i(Formats.Error_State_LoadFailed, component.getClass()), ex);
			return null;
		}
	}

	/**
	 * Reloads all components.
	 */
	public void reload()
	{
		onReload();
	}

	/**
	 * Fired when the plugin is loading, before it has been enabled.
	 */
	@Override
	public void onLoad()
	{
		if (isLoaded)
		{
			return;
		}
		isLoaded = true;

		loadComponents();

		super.onLoad();
	}

	/**
	 * Fired after the plugin is loaded, when it is being enabled.
	 */
	@Override
	public void onEnable()
	{
		for (final IComponent component : components)
		{
			try
			{
				component.onEnable();
			}
			catch (Throwable ex)
			{
				getLogger().log(Level.SEVERE, i(Formats.Error_State_EnableFailed, component.getClass()), ex);
			}
		}

		super.onEnable();
	}

	/**
	 * Fired when the plugin is being disabled.
	 */
	@Override
	public void onDisable()
	{
		for (final IComponent component : components)
		{
			try
			{
				component.onDisable();
			}
			catch (Throwable ex)
			{
				getLogger().log(Level.SEVERE, i(Formats.Error_State_DisableFailed, component.getClass()), ex);
			}
		}

		super.onDisable();
	}

	/**
	 * Fired when the plugin is to be reloaded.
	 */
	private void onReload()
	{
		reloadConfig();

		for (final IComponent component : components)
		{
			try
			{
				component.onReload();
			}
			catch (Throwable ex)
			{
				getLogger().log(Level.SEVERE, i(Formats.Error_State_ReloadFailed, component.getClass()), ex);
			}
		}
	}
}

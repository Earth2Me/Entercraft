package co.e2m.mc.entercraft.api;

import co.e2m.mc.entercraft.i18n.Formats;
import co.e2m.mc.entercraft.i18n.I18n;
import static co.e2m.mc.entercraft.i18n.I18n.i;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Base implementation for all Entercraft plugins.
 */
public abstract class ComponentsPlugin extends JavaPlugin implements IComponentsPlugin
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
	 * Used to prevent more than one call to onLoad.
	 */
	private transient boolean isLoaded = false;

	/**
	 * Loads all core components. Invoked when the plugin first loads.
	 *
	 * Be sure to call super's implementation first when overriding for internationalization support.
	 */
	protected void onLoadComponents()
	{
		i18n = addComponent(new I18n(this));
	}

	/**
	 * Gets a list of all tracked plugin components.
	 *
	 * @return an unmodifiable list of all tracked plugin components
	 */
	@Override
	public final List<IComponent> getComponents()
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
	@Override
	public final <T extends IComponent> T addComponent(final T component)
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
		catch (Exception ex)
		{
			getLogger().log(Level.SEVERE, i(Formats.Error_State_LoadFailed, component.getClass()), ex);
			return null;
		}
	}

	/**
	 * Reloads all components.
	 */
	@Override
	public final void reload()
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

		onLoadComponents();

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
			catch (Exception ex)
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
			catch (Exception ex)
			{
				getLogger().log(Level.SEVERE, i(Formats.Error_State_DisableFailed, component.getClass()), ex);
			}
		}

		super.onDisable();
	}

	/**
	 * Fired when the plugin is to be reloaded.
	 */
	protected void onReload()
	{
		reloadConfig();

		for (final IComponent component : components)
		{
			try
			{
				component.onReload();
			}
			catch (Exception ex)
			{
				getLogger().log(Level.SEVERE, i(Formats.Error_State_ReloadFailed, component.getClass()), ex);
			}
		}
	}
}

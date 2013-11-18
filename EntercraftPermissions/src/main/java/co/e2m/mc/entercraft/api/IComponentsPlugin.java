package co.e2m.mc.entercraft.api;

import java.util.List;
import org.bukkit.plugin.Plugin;


/**
 * Base for all Entercraft plugins.
 */
public interface IComponentsPlugin extends Plugin
{
	/**
	 * Gets a list of all tracked plugin components.
	 *
	 * @return an unmodifiable list of all tracked plugin components
	 */
	List<IComponent> getComponents();

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
	<T extends IComponent> T addComponent(final T component);

	/**
	 * Reloads all components.
	 */
	void reload();
}

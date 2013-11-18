package co.e2m.mc.entercraft.api;


/**
 * A plugin component interface that receives several plugin events.
 */
public interface IComponent
{
	/**
	 * Gets the associated plugin.
	 *
	 * @return the associated plugin
	 */
	IComponentsPlugin getPlugin();

	/**
	 * Fired when the component is ready to be enabled, but before it is enabled.
	 *
	 * No assumptions should be made about the state or readiness of other components.
	 */
	void onLoad();

	/**
	 * Fired when the component is enabled.
	 *
	 * The plugin can be considered stable and initialized.
	 */
	void onEnable();

	/**
	 * Fired when the component is disabled.
	 *
	 * Other components may already be disabled.
	 */
	void onDisable();

	/**
	 * Instructs the component to reload.
	 *
	 * The plugin can be considered stable and initialized, but no assumptions should be made about the order
	 * of reloading, or whether other components are reloading at all.
	 */
	void onReload();
}

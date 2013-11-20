package co.e2m.mc.entercraft.permissions.plugin;

import co.e2m.mc.entercraft.ComponentsPlugin;
import co.e2m.mc.entercraft.permissions.api.plugin.IEntercraftPermissionsPlugin;
import co.e2m.mc.entercraft.permissions.api.commands.IPermissionsCommand;
import co.e2m.mc.entercraft.permissions.commands.PermissionsCommand;
import lombok.Getter;


/**
 * Provides the main entrypoint and manager for the EntercraftPermissions Bukkit plugin.
 */
public final class EntercraftPermissionsPlugin extends ComponentsPlugin implements IEntercraftPermissionsPlugin
{
	/**
	 * @{inheritDoc}
	 */
	@Getter
	/**
	 * The command wrapper component.
	 */
	private transient IPermissionsCommand commandWrapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLoadComponents()
	{
		super.onLoadComponents();

		commandWrapper = addComponent(new PermissionsCommand(this));
	}
}
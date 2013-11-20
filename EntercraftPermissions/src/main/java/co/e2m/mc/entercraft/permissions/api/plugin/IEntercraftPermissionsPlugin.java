
package co.e2m.mc.entercraft.permissions.api.plugin;

import co.e2m.mc.entercraft.api.IComponentsPlugin;
import co.e2m.mc.entercraft.permissions.api.commands.IPermissionsCommand;


/**
 * Base for EntercraftPermissions plugin.
 */
public interface IEntercraftPermissionsPlugin extends IComponentsPlugin
{
	/**
	 * Gets the command wrapper component.  The command wrapper delegates subcommand handling.
	 *
	 * @return the command wrapper component
	 */
	IPermissionsCommand getCommandWrapper();
}

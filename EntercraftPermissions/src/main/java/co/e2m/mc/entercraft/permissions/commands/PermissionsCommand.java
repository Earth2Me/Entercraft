
package co.e2m.mc.entercraft.permissions.commands;

import co.e2m.mc.entercraft.api.commands.CommandWrapper;
import co.e2m.mc.entercraft.permissions.api.plugin.IEntercraftPermissionsPlugin;
import co.e2m.mc.entercraft.permissions.api.commands.IPermissionsCommand;


/**
 * Command wrapper for EntercraftPermissions plugin.
 */
public class PermissionsCommand extends CommandWrapper implements IPermissionsCommand
{
	private final IEntercraftPermissionsPlugin plugin;

	/**
	 * Instantiates a new EntercraftPermissions command wrapper.
	 *
	 * @param plugin the base plugin
	 */
	public PermissionsCommand(final IEntercraftPermissionsPlugin plugin)
	{
		super(plugin);

		this.plugin = plugin;
	}

	@Override
	protected void onLoadSubCommands()
	{
		// TODO: Add subcommands
	}
}

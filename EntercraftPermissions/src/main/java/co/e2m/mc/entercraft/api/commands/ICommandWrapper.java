
package co.e2m.mc.entercraft.api.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;


/**
 * Wraps subcommands with one base command.
 */
public interface ICommandWrapper extends CommandExecutor, TabCompleter
{
	/**
	 * Registers a new subcommand.
	 *
	 * @param command the subcommand to register
	 */
	void add(final ISubCommand command);

	/**
	 * Clears all subcommands.
	 */
	void clear();
}

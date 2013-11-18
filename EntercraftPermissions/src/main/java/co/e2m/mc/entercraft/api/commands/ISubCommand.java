package co.e2m.mc.entercraft.api.commands;

import java.util.Set;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;


/**
 * Provides a sub-command for a command wrapper.
 */
public interface ISubCommand extends CommandExecutor, TabCompleter
{
	/**
	 * Gets the primary name used to invoke the subcommand.
	 *
	 * @return the name of the subcommand
	 */
	String getName();

	/**
	 * Gets a list of aliases that can be used in addition to the primary name to invoke the subcommand.
	 *
	 * Should not include the primary name.  Should be immutable.
	 *
	 * @return a set of aliases that can be used to invoke the subcommand
	 */
	Set<String> getAliases();

	/**
	 * The description used to populate help text.
	 *
	 * @return the subcommand description
	 */
	String getDescription();
}

package co.e2m.mc.entercraft.permissions.commands;

import java.util.Set;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;


/**
 * Provides a sub-command for a command wrapper.
 */
public interface ISubCommand extends CommandExecutor, TabCompleter
{
	String getName();

	Set<String> getAliases();

	String getDescription();
}

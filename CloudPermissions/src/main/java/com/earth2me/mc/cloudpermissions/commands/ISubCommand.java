package com.earth2me.mc.cloudpermissions.commands;

import java.util.Set;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;


/**
 * Provides a sub-command for a command wrapper.
 */
public interface ISubCommand extends CommandExecutor, TabCompleter
{
	Set<String> getNames();
}

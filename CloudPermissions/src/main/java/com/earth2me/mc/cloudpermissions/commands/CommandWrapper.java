package com.earth2me.mc.cloudpermissions.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;


/**
 * Wraps all other commands with one base command, typically /cp.
 */
public final class CommandWrapper implements CommandExecutor, TabCompleter
{
	private static final String DEFAULT_SUBCOMMAND = "help";

	private final Map<String, ISubCommand> commands = new HashMap<>();

	public void add(final ISubCommand command)
	{
		for (final String name : command.getNames())
		{
			commands.put(name, command);
		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		final String subcommand;
		if (args == null || args.length < 1)
		{
			subcommand = DEFAULT_SUBCOMMAND;
		}
		else
		{
			subcommand = args[0].toLowerCase(Locale.US);
		}
		if (commands.containsKey(args))
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args)
	{
	}
}

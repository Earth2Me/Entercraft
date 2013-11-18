package co.e2m.mc.entercraft.permissions.commands;

import co.e2m.mc.entercraft.permissions.Component;
import co.e2m.mc.entercraft.permissions.EntercraftPermissionsPlugin;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;


/**
 * Wraps all other commands with one base command, typically /cp.
 */
public final class CommandWrapper extends Component implements CommandExecutor, TabCompleter
{
	/**
	 * Default subcommand used if none is specified.
	 */
	public static final String DEFAULT_SUBCOMMAND = "help";

	/**
	 * Maps names and aliases to subcommand handlers.
	 */
	private final Map<String, ISubCommand> subcommands = new HashMap<>();

	/**
	 * Instantiates a new command wrapper component.
	 *
	 * @param plugin the parent plugin
	 */
	public CommandWrapper(final EntercraftPermissionsPlugin plugin)
	{
		super(plugin);
	}

	/**
	 * Registers a new subcommand.
	 *
	 * @param command the subcommand to register
	 */
	public void add(final ISubCommand command)
	{
		for (final String alias : command.getAliases())
		{
			subcommands.put(alias, command);
		}

		subcommands.put(command.getName(), command);
	}

	/**
	 * Clears all subcommands.
	 */
	public void clear()
	{
		subcommands.clear();
	}

	/**
	 * Retrieves subcommand details from a command invocation.
	 *
	 * @param info command invocation information; typically arguments passed to on* methods
	 * @return contains subcommand handler and any information to be passed to it
	 */
	private CommandInfo getSubCommandInfo(final CommandInfo info)
	{
		final String[] args = info.getArgs();

		final String sublabel;
		if (args == null || args.length < 1)
		{
			sublabel = DEFAULT_SUBCOMMAND;
		}
		else
		{
			sublabel = args[0].toLowerCase(Locale.US);
		}

		if (!subcommands.containsKey(sublabel))
		{
			return null;
		}

		final ISubCommand subcommand = subcommands.get(sublabel);
		final String[] subargs;
		if (args != null && args.length > 1)
		{
			subargs = Arrays.copyOfRange(args, 1, args.length - 1);
		}
		else
		{
			subargs = new String[0];
		}

		return new CommandInfo(info.getSender(), info.getCommand(), sublabel, subargs, subcommand);
	}

	/**
	 * Triggered when a command is executed.  Delegates handling to subcommands.
	 *
	 * @param sender the initiator of the event
	 * @param command Bukkit's command information
	 * @param label the command name or alias used
	 * @param args any arguments passed with the command
	 * @return true if the command usage is correct or a custom error is emitted; otherwise, false
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		final CommandInfo info = getSubCommandInfo(new CommandInfo(sender, command, label, args, null));
		if (info == null)
		{
			return false;
		}

		return info.getSubCommand().onCommand(info.getSender(), info.getCommand(), info.getLabel(), info.getArgs());
	}

	/**
	 * Triggered when a user presses Tab while typing command arguments.  Delegates handling to subcommands.
	 *
	 * @param sender the initiator of the event
	 * @param command Bukkit's command information
	 * @param label the command name or alias used
	 * @param args any arguments passed with the command
	 * @return list of possible completion options for the next argument
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		final CommandInfo info = getSubCommandInfo(new CommandInfo(sender, command, label, args, null));
		if (info == null)
		{
			return Collections.emptyList();
		}

		return info.getSubCommand().onTabComplete(info.getSender(), info.getCommand(), info.getLabel(), info.getArgs());
	}

	/**
	 * Holds basic on* method arguments for commands, as well as an optional associated subcommand handler.
	 */
	@Data
	private class CommandInfo
	{
		private final CommandSender sender;
		private final Command command;
		private final String label;
		private final String[] args;
		private final ISubCommand subCommand;
	}
}

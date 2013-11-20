package co.e2m.mc.entercraft.api.commands;

import co.e2m.mc.entercraft.api.IComponentsPlugin;
import co.e2m.mc.entercraft.api.Component;
import co.e2m.mc.entercraft.api.commands.ICommandWrapper;
import co.e2m.mc.entercraft.api.commands.ISubCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


/**
 * Wraps all other commands under one base command.
 */
public abstract class CommandWrapper extends Component implements ICommandWrapper
{
	/**
	 * Gets all subcommands by their associated names and aliases.
	 *
	 * @return a mutable map of subcommands by their associated names and aliases
	 */
	@Getter(AccessLevel.PROTECTED)
	/**
	 * Maps names and aliases to subcommand handlers.
	 */
	private final Map<String, ISubCommand> subcommands = new HashMap<>();

	/**
	 * Instantiates a new command wrapper component.
	 *
	 * @param plugin the parent plugin
	 */
	public CommandWrapper(final IComponentsPlugin plugin)
	{
		super(plugin);
	}

	/**
	 * Gets the subcommand to use if no subcommand is specified.
	 *
	 * By default, this is "help".
	 *
	 * @return the default subcommand
	 */
	protected String getDefaultSubCommand()
	{
		return "help";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final ISubCommand command)
	{
		for (final String alias : command.getAliases())
		{
			subcommands.put(alias, command);
		}

		subcommands.put(command.getName(), command);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear()
	{
		subcommands.clear();
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void onLoad()
	{
		onLoadSubCommands();

		super.onLoad();
	}

	/**
	 * Load the subcommands.
	 */
	protected abstract void onLoadSubCommands();

	/**
	 * Retrieves subcommand details from a command invocation.
	 *
	 * @param info command invocation information; typically arguments passed to on* methods
	 * @return contains subcommand handler and any information to be passed to it
	 */
	protected CommandInfo getSubCommandInfo(final CommandInfo info)
	{
		final String[] args = info.getArgs();

		final String sublabel;
		if (args == null || args.length < 1)
		{
			sublabel = getDefaultSubCommand();
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
	protected static class CommandInfo
	{
		private final CommandSender sender;
		private final Command command;
		private final String label;
		private final String[] args;
		private final ISubCommand subCommand;

		/**
		 * Instantiates a new command info object.
		 *
		 * @param sender the original event sender
		 * @param command the original event command
		 * @param label the actual command alias used
		 * @param args any applicable arguments
		 * @param subCommand a subcommand, if applicable; otherwise, {@literal null}
		 */
		public CommandInfo(final CommandSender sender, final Command command, final String label, final String[] args, final ISubCommand subCommand)
		{
			this.sender = sender;
			this.command = command;
			this.label = label;
			this.args = Arrays.copyOf(args, args.length);
			this.subCommand = subCommand;
		}

		/**
		 * Gets the arguments to be passed to the next event receive.
		 *
		 * @return the arguments
		 */
		public String[] getArgs()
		{
			return Arrays.copyOf(args, args.length);
		}
	}
}
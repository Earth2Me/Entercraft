package co.e2m.mc.entercraft.permissions.api;

import co.e2m.mc.entercraft.i18n.I18n;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * One-dimensional information about a node.
 */
@Data
@AllArgsConstructor
public class NodeInfo
{
	/**
	 * A node with this name represents all descendent nodes of its parent.
	 */
	public static final String ALL_NAME = "*";

	/**
	 * A node with this name is the root node of a tree.
	 */
	public static final String ROOT_NAME = ".";

	/**
	 * Prefixes a node name to indicate that it is an exclude.
	 */
	public static final String EXCLUDE_PREFIX = "-";

	/**
	 * Used to locate priority modifiers in node text.
	 */
	private static final Pattern priorityRegex = Pattern.compile(":+");

	/**
	 * Gets the identifying name of the node.  Always lowercase.
	 *
	 * @return the name of the node
	 */
	private final String name;

	/**
	 * Gets the effect of the node (include or exclude).
	 *
	 * @return the effect of the node
	 */
	private NodeEffect effect;

	/**
	 * Gets the priority of the node, where a higher number indicates a higher priority.
	 *
	 * @return the priority of the node
	 */
	private int priority;

	/**
	 * Gets the path to the node, minus the current node itself.
	 *
	 * @return the ancestral path to the node, starting with the root node
	 */
	private final List<NodeInfo> ancestors;

	/**
	 * Gets the associated group.
	 *
	 * @return the associated group
	 */
	private final GroupId group;

	/**
	 * Instantiates a new node information object by parsing the provided text.
	 *
	 * @param group the associated group
	 * @param ancestors ancestors to the current node
	 * @param text raw node text to parse
	 */
	public NodeInfo(final GroupId group, final List<NodeInfo> ancestors, final String text)
	{
		assert group != null;
		assert ancestors != null;
		assert text != null;

		this.group = group;
		this.ancestors = Collections.unmodifiableList(ancestors);

		String name = text.toLowerCase(I18n.INVARIANT_LOCALE);
		final String[] tokens = priorityRegex.split(name, 2);

		Integer priority = null;
		if (tokens.length >= 2)
		{
			try
			{
				priority = Integer.parseInt(tokens[0]);
			}
			catch (final NumberFormatException ex)
			{
				priority = null;
			}

			name = tokens[1];
		}

		final NodeEffect effect;
		if (name.startsWith(EXCLUDE_PREFIX))
		{
			effect = NodeEffect.DENY;
			name = name.substring(1);
		}
		else
		{
			effect = NodeEffect.ALLOW;
		}

		if (priority == null)
		{
			priority = getDefaultPriority(name, effect);
		}

		this.name = name.intern();
		this.effect = effect;
		this.priority = priority;
	}

	/**
	 * Gets whether this is a root node.
	 *
	 * @return true if the node is a root node; otherwise, false
	 */
	public boolean isRoot()
	{
		return ROOT_NAME.equals(name);
	}

	/**
	 * Gets whether this node includes all children.
	 *
	 * @return true if the node represents all descendents of its parent; otherwise, false
	 */
	public boolean isAll()
	{
		return ALL_NAME.equals(name);
	}

	/**
	 * Merges with another node info object based on priority.
	 *
	 * @param other node info object with which to merge
	 */
	public void merge(final NodeInfo other)
	{
		assert other != null;

		if (priority > other.priority)
		{
			return;
		}

		if (priority == other.priority && getDefaultPriority(name, effect) >= getDefaultPriority(other.name, other.effect))
		{
			return;
		}

		setPriority(other.priority);
		setEffect(other.effect);
	}

	/**
	 * Gets the rule that results from this info object.
	 *
	 * @return the effective rule, or {@link Rule#NONE} if the node is inactive
	 */
	public Rule getRule()
	{
		if (effect == NodeEffect.NONE)
		{
			return Rule.NONE;
		}

		return new Rule(effect, priority);
	}

	/**
	 * Gets the path to the current node, including itself.
	 *
	 * @return the path to the node
	 */
	public List<NodeInfo> getPath()
	{
		final List<NodeInfo> path = new ArrayList<>(getAncestors());
		path.add(this);
		return path;
	}

	/**
	 * Gets the default priority based on a parsed name and effect.
	 *
	 * @param name parsed node name
	 * @param effect parsed node effect
	 * @return default priority
	 */
	private static int getDefaultPriority(final String name, final NodeEffect effect)
	{
		assert name != null;
		assert effect != null;

		if (ROOT_NAME.equals(name))
		{
			return 0;
		}

		switch (effect)
		{
		case ALLOW:
			return 1000;

		case DENY:
			return ALL_NAME.equals(name) ? 900 : 1100;

		default:
			return 0;
		}
	}
}

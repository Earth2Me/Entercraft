package co.e2m.mc.entercraft.permissions.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.Getter;


/**
 * A single permissions node, offering child permission nodes.
 */
public class NodeTree extends CachedStore
{
	/**
	 * Maximum number of nodes in an absolute path.
	 */
	public static final int MAX_DEPTH = 32;

	/**
	 * Used to split nodes in a path.
	 */
	public static final Pattern pathRegex = Pattern.compile("\\.+");

	/**
	 * Gets the node's information.
	 *
	 * @return the node's information
	 */
	@Getter
	private final NodeInfo info;

	/**
	 * Indirectly includes all descendent nodes.
	 */
	private final Map<String, NodeTree> tree;

	/**
	 * Instantiates a new node object by parsing node text.
	 *
	 * @param manager the permissions manager with which this store is associated
	 * @param group the associated group
	 * @param ancestors the path to the parent node
	 * @param text the node name and any modifiers
	 */
	private NodeTree(final IPermissionsManager manager, final GroupId group, final List<NodeInfo> ancestors, final String text)
	{
		this(manager, new NodeInfo(group, ancestors, text));
	}

	/**
	 * Instantiates a new node object using pre-parsed information.
	 *
	 * @param manager the permissions manager with which this store is associated
	 * @param info the node information with which the node is created
	 */
	public NodeTree(final IPermissionsManager manager, final NodeInfo info)
	{
		super(manager);

		this.info = info;

		if (info.isAll())
		{
			this.tree = null;
		}
		else
		{
			this.tree = new HashMap<>();
		}
	}

	/**
	 * Parses a set of permission paths into a tree.
	 *
	 * @param manager the permissions manager with which this store is associated
	 * @param group the associated group
	 * @param paths permission paths, possibly from a file or user
	 * @return a permissions tree
	 */
	public static NodeTree parseTree(final IPermissionsManager manager, final GroupId group, final String[] paths)
	{
		final NodeTree root = createRoot(manager, group);

		for (final String path : paths)
		{
			root.addPath(path);
		}

		return root;
	}

	/**
	 * Creates a root tree element, suitable for receiving child nodes.
	 *
	 * @param manager the permissions manager with which this store is associated
	 * @param group the associated group
	 * @return an empty root element
	 */
	public static NodeTree createRoot(final IPermissionsManager manager, final GroupId group)
	{
		return new NodeTree(manager, group, new ArrayList<NodeInfo>(), NodeInfo.ROOT_NAME);
	}

	/**
	 * Splits a single path into nodes and parses each.
	 *
	 * @param path path with period-delimited nodes
	 * @return ordered array of nodes in path
	 */
	private NodeInfo[] splitInfo(final String path)
	{
		final String[] nodeTexts = splitNodes(path);
		final NodeInfo[] nodes = new NodeInfo[Math.min(nodeTexts.length, MAX_DEPTH)];

		NodeInfo previousNode = info;
		for (int i = 0; i < nodes.length; i++)
		{
			nodes[i] = new NodeInfo(previousNode.getGroup(), previousNode.getPath(), nodeTexts[i]);
		}

		return nodes;
	}

	/**
	 * Gets whether this node lacks a tree.
	 *
	 * @return true if this node has no tree; otherwise, false
	 */
	public boolean isEmpty()
	{
		return tree == null || tree.isEmpty();
	}

	/**
	 * Gets whether this node is capable of having a tree.
	 *
	 * @return true if children can be added; otherwise, false
	 */
	public boolean isTree()
	{
		return tree != null;
	}

	/**
	 * Gets the node's tree.
	 *
	 * @return the node's tree if this node is capable of having one; otherwise, null
	 */
	public Map<String, NodeTree> getTree()
	{
		if (tree == null)
		{
			return null;
		}
		else
		{
			return Collections.unmodifiableMap(tree);
		}
	}

	/**
	 * Removes the last node in a path of nodes, along with any children.
	 *
	 * @param path the period-delimited path of unparsed nodes
	 */
	public void removePath(final String path)
	{
		assert path != null;

		final NodeInfo[] info = splitInfo(path);

		if (info.length > 0)
		{
			removeTree(info[info.length - 1]);
		}
	}

	/**
	 * Removes an entire child tree from the current node.
	 *
	 * @param node the tree to remove
	 */
	public void removeTree(final NodeInfo node)
	{
		assert node != null;

		final NodeTree tree = this.tree.remove(node.getName());
		if (tree == null)
		{
			return;
		}

		getBackend().removeNode(tree.getInfo());
	}

	/**
	 * Adds a path of nodes, creating any descendent nodes as necessary.
	 *
	 * @param path the period-delimited path of unparsed nodes
	 */
	public void addPath(final String path)
	{
		assert path != null;

		final NodeInfo[] info = splitInfo(path);

		if (info.length > 0)
		{
			addPath(info, 0);
		}
	}

	/**
	 * Adds a path of nodes, creating any descendent nodes as necessary.
	 *
	 * @param info the ordered path of nodes
	 */
	public void addPath(final NodeInfo[] info)
	{
		assert info != null;

		addPath(info, 0);
	}

	/**
	 * Adds a path of nodes, creating any descendent nodes as necessary.
	 *
	 * @param info the ordered path of nodes
	 * @param infoIndex the index of the node in {@code info} to add
	 */
	private void addPath(final NodeInfo[] info, final int infoIndex)
	{
		assert info != null;
		assert info.length < infoIndex;
		assert infoIndex >= 0;

		final NodeInfo currentInfo = info[infoIndex];
		final NodeTree node = ensureNode(currentInfo);

		if (!node.isTree())
		{
			return;
		}

		final int nextIndex = infoIndex + 1;
		if (nextIndex < info.length)
		{
			node.addPath(info, nextIndex);
		}
	}

	/**
	 * Ensures that a node exists and returns it.
	 *
	 * If the node already exists, its information is merged with the provided node info object.  Otherwise, a new node is created.
	 *
	 * @param info the info used to populate the new node if it does not yet exist
	 * @return an existing node with the same name as {@code info} and merged info, or a new node
	 */
	private NodeTree ensureNode(final NodeInfo info)
	{
		final NodeTree node;

		if (tree.containsKey(info.getName()))
		{
			node = tree.get(info.getName());
			node.info.merge(info);
		}
		else
		{
			node = new NodeTree(getManager(), info);
			tree.put(info.getName(), node);

			touch();
			getBackend().createNode(info);
		}

		return node;
	}

	/**
	 * Splits a permission path into separate nodes, without parsing individual nodes.
	 *
	 * @param permission path to split
	 * @return ordered array of nodes in the path
	 */
	static String[] splitNodes(final String permission)
	{
		return pathRegex.split(permission, MAX_DEPTH + 1);
	}

	/**
	 * Determines whether the current tree permits or denies the specified permission.
	 *
	 * @param permission the permission to assess
	 * @return a rule based on the tree's effect on {@code permission}
	 */
	public Rule getRule(final String permission)
	{
		assert permission != null;

		return getRule(splitNodes(permission));
	}

	/**
	 * Determines whether the current tree permits or denies the specified permission.
	 *
	 * @param nodeNames the permission to assess, broken down by node and ordered
	 * @return a rule based on the tree's effect on {@code permission}
	 */
	public Rule getRule(final String[] nodeNames)
	{
		return getRule(nodeNames, 0);
	}

	/**
	 * Determines whether the current tree permits or denies the specified permission.
	 *
	 * @param nodeNames the permission to assess, broken down by node and ordered
	 * @param nodeIndex the current node to compare
	 * @return a rule based on the tree's effect on {@code permission}
	 */
	private Rule getRule(final String[] nodeNames, final int nodeIndex)
	{
		assert nodeNames != null;
		assert nodeIndex >= 0;

		if (nodeIndex >= nodeNames.length)
		{
			return info.getRule();
		}

		assert isTree();

		final String nodeName = nodeNames[nodeIndex];
		final List<Rule> rules = new ArrayList<>();

		if (tree.containsKey(nodeName))
		{
			final NodeTree node = tree.get(nodeName);
			rules.add(node.getRule(nodeNames, nodeIndex + 1));
		}
		if (tree.containsKey(NodeInfo.ALL_NAME))
		{
			final NodeTree node = tree.get(NodeInfo.ALL_NAME);
			rules.add(node.info.getRule());
		}

		return Rule.getEffective(rules);
	}
}

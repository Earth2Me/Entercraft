package co.e2m.mc.entercraft.permissions.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.permission.Permission;


/**
 * Base for Entercraft's cloud-based permissions handlers.
 */
public abstract class PermissionsManager extends Permission implements IPermissionsManager
{
	/**
	 * The default default ID.  No, that's not a typo.  The default ID can be changed via configuration.
	 */
	private static final String DEFAULT_ID = "default";

	/**
	 * Gets the group cache.
	 *
	 * @return the group cache
	 */
	@Getter(AccessLevel.PROTECTED)
	private final Map<GroupId, Group> cache = new HashMap<>();

	/**
	 * Gets a mapping of world names to world IDs.
	 *
	 * @return map of world names to world IDs
	 */
	@Getter(AccessLevel.PROTECTED)
	private final Map<String, String> worldIds = new HashMap<>();

	/**
	 * Gets the default world ID.  Used if a world doesn't have an associated ID.
	 *
	 * @return the default world ID
	 */
	@Getter(AccessLevel.PROTECTED)
	/**
	 * Sets the default world ID.  Used if a world doesn't have an associated ID.
	 *
	 * @param defaultWorldId the new default world ID
	 */
	@Setter(AccessLevel.PROTECTED)
	private String defaultWorldId = DEFAULT_ID;

	/**
	 * Gets the default group ID.  If this group exists, users default to this group.
	 *
	 * @return the default group ID
	 */
	@Getter(AccessLevel.PROTECTED)
	/**
	 * Sets the default group ID.  If this group exists, users default to this group.
	 *
	 * @param defaultGroupId the default group ID
	 */
	@Setter(AccessLevel.PROTECTED)
	private GroupId defaultGroupId = new GroupId(defaultWorldId, GroupType.NORMAL, DEFAULT_ID);

	/**
	 * {@inheritDoc}
	 */
	@Getter
	/**
	 * {@inheritDoc}
	 */
	@Setter
	private boolean enabled;

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean hasSuperPermsCompat()
	{
		return false;
	}

	/**
	 * Converts a world name to world ID.
	 *
	 * @param world the name of the world
	 * @return the ID of the world
	 */
	protected String getWorldId(final String world)
	{
		if (world != null && worldIds.containsKey(world))
		{
			return worldIds.get(world);
		}
		else
		{
			return defaultWorldId;
		}
	}

	/**
	 * Gets a group identifier.
	 *
	 * @param world world name, not ID
	 * @param type group type
	 * @param name relative group name
	 * @return server-unique group identifier
	 */
	protected GroupId getGroupId(final String world, final GroupType type, final String name)
	{
		return new GroupId(getWorldId(world), type, name);
	}

	/**
	 * Gets a group permission tree.
	 *
	 * @param world world name, not ID
	 * @param type group type
	 * @param name relative group name
	 * @return the group if it exists; otherwise, null
	 */
	protected Group getGroup(final String world, final GroupType type, final String name)
	{
		return getGroup(getGroupId(world, type, name));
	}

	/**
	 * Gets a group permission tree.
	 *
	 * @param id the group's identifier
	 * @return the group if it exists; otherwise, null
	 */
	protected Group getGroup(final GroupId id)
	{
		if (cache.containsKey(id))
		{
			return cache.get(id);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets the default group.
	 *
	 * @return the default group, or null if the group doesn't exist
	 */
	protected Group getDefaultGroup()
	{
		final GroupId id = getDefaultGroupId();
		return getGroup(id);
	}

	/**
	 * Gets a group permission tree.
	 *
	 * @param world world name, not ID
	 * @param type group type
	 * @param name relative group name
	 * @return the group if it exists; otherwise, the default group
	 */
	protected Group getGroupOrDefault(final String world, final GroupType type, final String name)
	{
		return getGroupOrDefault(getGroupId(world, type, name));
	}

	/**
	 * Gets a group permission tree.
	 *
	 * @param id the group's identifier
	 * @return the group if it exists; otherwise, the default group
	 */
	protected Group getGroupOrDefault(final GroupId id)
	{
		final Group group = getGroup(id);
		if (group == null)
		{
			return getDefaultGroup();
		}
		else
		{
			return group;
		}
	}

	/**
	 * Gets a group permission tree, creating it if it doesn't already exist.
	 *
	 * @param world world name, not ID
	 * @param type group type
	 * @param name relative group name
	 * @return the group if it exists; otherwise, a new group
	 */
	protected Group getGroupOrCreate(final String world, final GroupType type, final String name)
	{
		return getGroupOrCreate(getGroupId(world, type, name));
	}

	/**
	 * Gets a group permission tree, creating it if it doesn't already exist.
	 *
	 * @param id the group's identifier
	 * @return the group if it exists; otherwise, a new group
	 */
	protected Group getGroupOrCreate(final GroupId id)
	{
		Group group = getGroup(id);

		if (group == null)
		{
			group = new Group(this, id);
			onGroupCreated(group);
		}

		return group;
	}

	/**
	 * Adds a group to the backend.
	 *
	 * @param group the group to store
	 */
	protected void onGroupCreated(final Group group)
	{
		getBackend().createGroup(group);
	}

	/**
	 * Removes a group from the backend.
	 *
	 * @param group the group to store
	 */
	protected void onGroupRemoved(final Group group)
	{
		for (final Group g : cache.values())
		{
			if (g.isInherited(group))
			{
				g.uninherit(group);
			}
		}

		getBackend().removeGroup(group);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean playerHas(final String world, final String player, final String permission)
	{
		final Group group = getGroupOrDefault(world, GroupType.USER, player);
		if (group == null)
		{
			return false;
		}

		return group.getRule(permission).getEffect() == NodeEffect.ALLOW;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean playerAdd(final String world, final String player, final String permission)
	{
		final Group group = getGroupOrCreate(world, GroupType.USER, player);
		group.getPermissions().addPath(permission);
		return true;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean playerRemove(final String world, final String player, final String permission)
	{
		final Group group = getGroup(world, GroupType.USER, player);
		if (group == null)
		{
			return false;
		}

		group.getPermissions().removePath(permission);
		return true;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean groupHas(String world, String group, String permission)
	{
		final Group g = getGroup(world, GroupType.NORMAL, group);
		if (g == null)
		{
			return false;
		}

		return g.getRule(permission).getEffect() == NodeEffect.ALLOW;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean groupAdd(String world, String group, String permission)
	{
		final Group g = getGroup(world, GroupType.NORMAL, group);
		if (g == null)
		{
			return false;
		}

		g.getPermissions().addPath(permission);
		return true;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean groupRemove(String world, String group, String permission)
	{
		final Group g = getGroup(world, GroupType.NORMAL, group);
		if (g == null)
		{
			return false;
		}

		g.getPermissions().removePath(permission);
		return true;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean playerInGroup(String world, String player, String group)
	{
		final Group g = getGroup(world, GroupType.NORMAL, group);
		final Group user = getGroup(world, GroupType.USER, player);
		return g != null && user != null && user.isInherited(user);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean playerAddGroup(String world, String player, String group)
	{
		final Group g = getGroup(world, GroupType.NORMAL, group);
		final Group user = getGroup(world, GroupType.USER, player);
		return user.inherit(g);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean playerRemoveGroup(String world, String player, String group)
	{
		final Group g = getGroup(world, GroupType.NORMAL, group);
		final Group user = getGroup(world, GroupType.USER, player);
		return user.uninherit(g);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public String[] getPlayerGroups(String world, String player)
	{
		final Group user = getGroup(world, GroupType.USER, player);
		final Set<String> groups = user.getGroupNames();
		return groups.toArray(new String[groups.size()]);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public String getPrimaryGroup(String world, String player)
	{
		final Group user = getGroup(world, GroupType.USER, player);
		final Group parent = user.getPrimaryParent();

		if (parent == null)
		{
			return null;
		}

		return parent.toString();
	}

	/**
	 * @{inheritDoc}
	 *
	 * @deprecated This is not useful, as it can only obtain a list of groups that have been cached.
	 */
	@Deprecated
	@Override
	public String[] getGroups()
	{
		final GroupId[] keys = cache.keySet().toArray(new GroupId[cache.size()]);
		final String[] names = new String[keys.length];

		for (int i = 0; i < names.length; i++)
		{
			names[i] = keys[i].toString();
		}

		return names;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean hasGroupSupport()
	{
		return true;
	}
}

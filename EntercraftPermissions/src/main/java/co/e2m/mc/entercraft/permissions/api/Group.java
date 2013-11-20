package co.e2m.mc.entercraft.permissions.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;


/**
 * Permissions model for an individual group.
 */
public class Group extends CachedStore
{
	/**
	 * Gets a set of groups from which this group inherits permissions.
	 *
	 * @return a set of inherited groups
	 */
	private final Set<Group> parents = new HashSet<>();

	/**
	 * Gets the group's own permission tree.
	 *
	 * @return the group's permission tree
	 */
	@Getter
	private final NodeTree permissions;

	/**
	 * Gets the ID of the group.
	 *
	 * @return the group's identifier
	 */
	@Getter
	private final GroupId id;

	/**
	 * Group metadata, such as prefix, suffix, primary group (for users), etc.
	 */
	private final Map<String, Serializable> data = new HashMap<>();

	/**
	 * Gets the group's primary parent.  Metadata is only inherited from this group.
	 *
	 * <p>
	 * This group must be a directly inherited group for inheritance to function properly.
	 * </p>
	 *
	 * @return the group's primary parent
	 */
	@Getter
	private Group primaryParent;

	/**
	 * Creates a new permissions group from a group identifier.
	 *
	 * @param manager the permissions manager with which this store is associated
	 * @param id the group's identifier
	 */
	public Group(final IPermissionsManager manager, final GroupId id)
	{
		super(manager);

		this.id = id;
		this.permissions = NodeTree.createRoot(manager, id);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return the identifying canonical name of the group
	 */
	@Override
	public String toString()
	{
		return getId().getCanonicalName();
	}

	/**
	 * Sets the group's primary parent, inheriting from it if necessary.
	 *
	 * @param group the parent group, or {@literal null} to clear the primary parent
	 */
	public void setPrimaryParent(final Group group)
	{
		if (getPrimaryParent() == group)
		{
			return;
		}

		if (group != null && !parents.contains(group))
		{
			inherit(group);
		}

		primaryParent = group;
		getBackend().updatePrimaryParent(this, group);
	}

	/**
	 * Gets group metadata.
	 *
	 * <p>
	 * Note the group metadata is only inherited from a primary parent.
	 * </p>
	 *
	 * @param <T> the type of object to retrieve
	 * @param key the key of the object; case-sensitive
	 * @param type the type of object to retrieve
	 * @return the data value if it exists; otherwise, {@literal null}
	 */
	public <T extends Serializable> T getData(final String key, final Class<T> type)
	{
		if (!data.containsKey(key))
		{
			return null;
		}

		final Serializable value = data.get(key);
		if (value == null || !type.isAssignableFrom(value.getClass()))
		{
			return null;
		}

		return type.cast(value);
	}

	/**
	 * Stores group metadata.
	 *
	 * @param <T> the type of object to store
	 * @param key the key of the object; case-sensitive
	 * @param type the type of object to store
	 * @param value the value to store, or null to remove the entry
	 */
	public <T extends Serializable> void setData(final String key, final Class<T> type, T value)
	{
		if (value == null)
		{
			removeData(key);
			return;
		}

		final boolean isUpdate = data.containsKey(key);
		if (isUpdate && value.equals(data.get(key)))
		{
			return;
		}

		data.put(key, value);

		if (isUpdate)
		{
			getBackend().updateGroupData(this, key, type, value);
		}
		else
		{
			getBackend().addGroupData(this, key, type, value);
		}
	}

	/**
	 * Removes group metadata.
	 *
	 * @param key the key to remove
	 */
	public void removeData(final String key)
	{
		if (!data.containsKey(key))
		{
			return;
		}

		data.remove(key);
		getBackend().removeGroupData(this, key);
	}

	/**
	 * Determines whether this group inherits from another group.
	 *
	 * @param group the group from which this group may inherit
	 * @return true if this group inherits from the specified group; otherwise, false
	 */
	public boolean isInherited(final Group group)
	{
		if (parents.contains(group))
		{
			return true;
		}

		final Set<Group> checkedGroups = new HashSet<>();
		checkedGroups.add(this);
		return isInherited(group, checkedGroups);
	}

	/**
	 * Determines whether this group inherits from another group.
	 *
	 * @param group the group from which this group may inherit
	 * @param checkedGroups groups that have already been checked, to prevent infinite recursion
	 * @return true if this group inherits from the specified group; otherwise, false
	 */
	private boolean isInherited(final Group group, final Set<Group> checkedGroups)
	{
		assert group != null;
		assert checkedGroups != null;

		if (getPermissions().isEmpty())
		{
			return false;
		}

		final Set<Group> groupsToCheck = new HashSet<>(parents);
		groupsToCheck.removeAll(checkedGroups);
		checkedGroups.addAll(groupsToCheck);

		for (final Group g : groupsToCheck)
		{
			if (g.isInherited(group, checkedGroups))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets this group and all inherited groups, direct or indirect.
	 *
	 * @return this group and all inherited groups
	 */
	public Set<Group> getGroups()
	{
		final Set<Group> groups = new HashSet<>();
		groups.add(this);

		for (final Group group : parents)
		{
			if (groups.contains(group))
			{
				continue;
			}

			groups.addAll(group.getGroups());
		}

		return Collections.unmodifiableSet(groups);
	}

	/**
	 * Gets this group name and all inherited group names, direct or indirect.
	 *
	 * @return this group and all inherited group names
	 */
	public Set<String> getGroupNames()
	{
		final Set<String> groups = new HashSet<>();
		groups.add(getId().getCanonicalName());

		for (final Group group : parents)
		{
			if (groups.contains(group.getId().getCanonicalName()))
			{
				continue;
			}

			groups.addAll(group.getGroupNames());
		}

		return Collections.unmodifiableSet(groups);
	}

	/**
	 * Inherit rules from another group.
	 *
	 * @param group the group from which to inherit rules
	 * @return true if the change was successful; otherwise, false
	 */
	public boolean inherit(final Group group)
	{
		assert group != null;

		if (parents.contains(group))
		{
			return false;
		}

		parents.add(group);
		getBackend().addInheritedGroup(this, group);
		return true;
	}

	/**
	 * Uninherits rules from another group.
	 *
	 * @param group the group from which to uninherit rules
	 * @return true if the change was successful; otherwise, false
	 */
	public boolean uninherit(final Group group)
	{
		assert group != null;

		if (!parents.contains(group))
		{
			return false;
		}

		parents.remove(group);
		getBackend().removeInheritedGroup(this, group);
		return true;
	}

	/**
	 * Determines whether the group permits or denies the specified permission.
	 *
	 * @param permission the permission to assess
	 * @return a rule based on the tree's effect on {@code permission}
	 */
	public Rule getRule(final String permission)
	{
		assert permission != null;

		final String[] nodes = NodeTree.splitNodes(permission);
		final List<Rule> rules = new ArrayList<>();

		for (final Group group : getGroups())
		{
			rules.add(group.getPermissions().getRule(nodes));
		}

		return Rule.getEffective(rules);
	}
}

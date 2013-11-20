package co.e2m.mc.entercraft.permissions.api;

import java.io.Serializable;


/**
 * Manages a permissions backend, performing all updates and queries.
 */
public interface IPermissionsBackend
{
	/**
	 * Creates a new group.
	 *
	 * @param group the group to create
	 */
	void createGroup(Group group);

	/**
	 * Creates a new node.  The parent node/group is guaranteed to exist.
	 *
	 * @param node the node to create
	 */
	void createNode(NodeInfo node);

	/**
	 * Removes a group.  The group is guaranteed to have been removed from other groups.
	 *
	 * @param group the group to remove
	 */
	void removeGroup(Group group);

	/**
	 * Removes a node.  The node may have children.
	 *
	 * @param node the node to remove
	 */
	void removeNode(NodeInfo node);

	/**
	 * Updates a node's priority and effect.  The node is guaranteed to exist.
	 *
	 * @param node the node to update
	 */
	void updateNode(NodeInfo node);

	/**
	 * Adds a group to another group's inheritance list.
	 *
	 * @param parent the group to modify
	 * @param child the group to add
	 */
	void addInheritedGroup(Group parent, Group child);

	/**
	 * Removes a group from another group's inheritance list.
	 *
	 * @param parent the group to modify
	 * @param child the group to remove
	 */
	void removeInheritedGroup(Group parent, Group child);

	/**
	 * Updates a group's primary parent.  The target group is guaranteed to inherit from its parent.
	 *
	 * @param self the group to modify
	 * @param parent the parent group
	 */
	void updatePrimaryParent(Group self, Group parent);

	/**
	 * Adds a metadata entry to a group.  The key is guaranteed to be nonexistent.
	 *
	 * @param <T> the type of data to store
	 * @param group the group that is being modified
	 * @param key the identifier under which to store the data; case-sensitive
	 * @param type the type of data to store
	 * @param value the data to store
	 */
	<T extends Serializable> void addGroupData(Group group, String key, Class<T> type, T value);

	/**
	 * Updates a group's existing metadata entry.  The type may differ, but the key is guaranteed to exist.
	 *
	 * @param <T> the type of data to store
	 * @param group the group that is being modified
	 * @param key the identifier under which to store the data; case-sensitive
	 * @param type the type of data to store
	 * @param value the data to store
	 */
	<T extends Serializable> void updateGroupData(Group group, String key, Class<T> type, T value);

	/**
	 * Removes a group's existing metadata entry.  The key is guaranteed to exist.
	 *
	 * @param group the group that is being modified
	 * @param key the identifier to delete; case-sensitive
	 */
	void removeGroupData(Group group, String key);
}

/*
 * This file is part of Vault.
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 *
 * -------------------------------------------------------------------------
 *
 * This file has been adapted from <https://github.com/MilkBowl/Vault/blob/master/src/net/milkbowl/vault/permission/Permission.java> by
 * Paul Buonopane, and is not officially part of Vault as of writing.  It remains licensed under the LGPL.  If this file is ever integrated
 * into the official Vault source code, this notice can be removed.
 */
package net.milkbowl.vault.permission;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * The main Permission API - allows for group and player based permission tests
 */
public interface IPermission
{
	/**
	 * Gets name of permission method
	 *
	 * @return Name of Permission Method
	 */
	String getName();

	/**
	 * Checks if permission method is enabled.
	 *
	 * @return Success or Failure
	 */
	boolean isEnabled();

	/**
	 * Returns if the permission system is or attempts to be compatible with super-perms.
	 *
	 * @return True if this permission implementation works with super-perms
	 */
	boolean hasSuperPermsCompat();

	/**
	 * Checks if player has a permission node. (Short for playerHas(...) Supports NULL value for World if the permission system registered
	 * supports global permissions. But May return odd values if the servers registered permission system does not have a global permission
	 * store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean has(String world, String player, String permission);

	/**
	 * Checks if player has a permission node. (Short for playerHas(...) Supports NULL value for World if the permission system registered
	 * supports global permissions. But May return odd values if the servers registered permission system does not have a global permission
	 * store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean has(World world, String player, String permission);

	/**
	 * Checks if a CommandSender has a permission node. This will return the result of bukkits, generic .hasPermission() method and is
	 * identical in all cases. This method will explicitly fail if the registered permission system does not register permissions in bukkit.
	 *
	 * For easy checking of a commandsender
	 *
	 * @param sender
	 * @param permission
	 * @return true if the sender has the permission
	 */
	boolean has(CommandSender sender, String permission);

	/**
	 * Checks if player has a permission node. (Short for playerHas(...)
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean has(Player player, String permission);

	/**
	 * Checks if player has a permission node. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerHas(String world, String player, String permission);

	/**
	 * Checks if player has a permission node. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerHas(World world, String player, String permission);

	/**
	 * Checks if player has a permission node.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerHas(Player player, String permission);

	/**
	 * Add permission to a player. Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerAdd(String world, String player, String permission);

	/**
	 * Add permission to a player. Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerAdd(World world, String player, String permission);

	/**
	 * Add permission to a player ONLY for the world the player is currently on. This is a world-specific operation, if you want to add
	 * global permission you must explicitly use NULL for the world.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerAdd(Player player, String permission);

	/**
	 * Add transient permission to a player. This implementation can be used by any subclass which implements a "pure" superperms plugin,
	 * i.e. one that only needs the built-in Bukkit API to add transient permissions to a player. Any subclass implementing a plugin which
	 * provides its own API for this needs to override this method.
	 *
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerAddTransient(String player, String permission);

	/**
	 * Add transient permission to a player. This operation adds a world-unspecific permission onto the player object in bukkit via Bukkit's
	 * permission interface.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerAddTransient(Player player, String permission);

	/**
	 * Adds a world specific transient permission to the player - ONLY WORKS IN PEX/P3 - otherwise it defaults to GLOBAL!
	 *
	 * @param worldName
	 * @param player
	 * @param permission
	 * @return Success or Failure
	 */
	boolean playerAddTransient(String worldName, Player player, String permission);

	/**
	 * Adds a world specific transient permission to the player - ONLY WORKS IN PEX/P3 - otherwise it defaults to GLOBAL!
	 *
	 * @param worldName
	 * @param player
	 * @param permission
	 * @return Success or Failure
	 */
	boolean playerAddTransient(String worldName, String player, String permission);

	/**
	 * Removes a world specific transient permission from the player - Only works in PEX/P3 - otherwise it defaults to Global!
	 *
	 * @param worldName
	 * @param player
	 * @param permission
	 * @return Success or Failure
	 */
	boolean playerRemoveTransient(String worldName, String player, String permission);

	/**
	 * Removes a world specific transient permission from the player - Only works in PEX/P3 - otherwise it defaults to Global!
	 *
	 * @param worldName
	 * @param player
	 * @param permission
	 * @return Success or Failure
	 */
	boolean playerRemoveTransient(String worldName, Player player, String permission);

	/**
	 * Remove permission from a player.
	 *
	 * @param world World name
	 * @param player Name of Player
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerRemove(String world, String player, String permission);

	/**
	 * Remove permission from a player. Supports NULL value for World if the permission system registered supports global permissions. But
	 * May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerRemove(World world, String player, String permission);

	/**
	 * Remove permission from a player. Will attempt to remove permission from the player on the player's current world. This is NOT a
	 * global operation.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerRemove(Player player, String permission);

	/**
	 * Remove transient permission from a player. This implementation can be used by any subclass which implements a "pure" superperms
	 * plugin, i.e. one that only needs the built-in Bukkit API to remove transient permissions from a player. Any subclass implementing a
	 * plugin which provides its own API for this needs to override this method.
	 *
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerRemoveTransient(String player, String permission);

	/**
	 * Remove transient permission from a player.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean playerRemoveTransient(Player player, String permission);

	/**
	 * Checks if group has a permission node. Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean groupHas(String world, String group, String permission);

	/**
	 * Checks if group has a permission node. Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean groupHas(World world, String group, String permission);

	/**
	 * Add permission to a group. Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean groupAdd(String world, String group, String permission);

	/**
	 * Add permission to a group. Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean groupAdd(World world, String group, String permission);

	/**
	 * Remove permission from a group. Supports NULL value for World if the permission system registered supports global permissions. But
	 * May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean groupRemove(String world, String group, String permission);

	/**
	 * Remove permission from a group. Supports NULL value for World if the permission system registered supports global permissions. But
	 * May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	boolean groupRemove(World world, String group, String permission);

	/**
	 * Check if player is member of a group. Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * This method is known to return unexpected results depending on what permission system is being used. Different permission systems
	 * will store the player groups differently, It is HIGHLY suggested you test your code out first.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerInGroup(String world, String player, String group);

	/**
	 * Check if player is member of a group. Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerInGroup(World world, String player, String group);

	/**
	 * Check if player is member of a group. This method will ONLY check groups for which the player is in that are defined for the current
	 * world. This may result in odd return behaviour depending on what permission system has been registered.
	 *
	 * @param player Player Object
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerInGroup(Player player, String group);

	/**
	 * Add player to a group. Supports NULL value for World if the permission system registered supports global permissions. But May return
	 * odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerAddGroup(String world, String player, String group);

	/**
	 * Add player to a group. Supports NULL value for World if the permission system registered supports global permissions. But May return
	 * odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerAddGroup(World world, String player, String group);

	/**
	 * Add player to a group. This will add a player to the group on the current World. This may return odd results if the permission system
	 * being used on the server does not support world-specific groups, or if the group being added to is a global group.
	 *
	 * @param player Player Object
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerAddGroup(Player player, String group);

	/**
	 * Remove player from a group. Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerRemoveGroup(String world, String player, String group);

	/**
	 * Remove player from a group. Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerRemoveGroup(World world, String player, String group);

	/**
	 * Remove player from a group. This will add a player to the group on the current World. This may return odd results if the permission
	 * system being used on the server does not support world-specific groups, or if the group being added to is a global group.
	 *
	 * @param player Player Object
	 * @param group Group name
	 * @return Success or Failure
	 */
	boolean playerRemoveGroup(Player player, String group);

	/**
	 * Gets the list of groups that this player has. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Array of groups
	 */
	String[] getPlayerGroups(String world, String player);

	/**
	 * Gets the list of groups that this player has Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @return Array of groups
	 */
	String[] getPlayerGroups(World world, String player);

	/**
	 * Returns a list of world-specific groups that this player is currently in. May return unexpected results if you are looking for global
	 * groups, or if the registered permission system does not support world-specific groups.
	 *
	 * @param player Player Object
	 * @return Array of groups
	 */
	String[] getPlayerGroups(Player player);

	/**
	 * Gets players primary group Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Players primary group
	 */
	String getPrimaryGroup(String world, String player);

	/**
	 * Gets players primary group Supports NULL value for World if the permission system registered supports global permissions. But May
	 * return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World Object
	 * @param player Player name
	 * @return Players primary group
	 */
	String getPrimaryGroup(World world, String player);

	/**
	 * Get players primary group
	 *
	 * @param player Player Object
	 * @return Players primary group
	 */
	String getPrimaryGroup(Player player);

	/**
	 * Returns a list of all known groups
	 *
	 * @return an Array of String of all groups
	 */
	String[] getGroups();

	/**
	 * Returns true if the given implementation supports groups.
	 *
	 * @return true if the implementation supports groups
	 */
	boolean hasGroupSupport();
}

package co.e2m.mc.entercraft.permissions.api;


/**
 * Determines the effect that a permissions tree has.
 */
public enum NodeEffect implements Comparable<NodeEffect>
{
	/**
	 * The rule has no effect.
	 */
	NONE,

	/**
	 * Allow the permission.
	 */
	ALLOW,

	/**
	 * Deny the permission.
	 */
	DENY;
}

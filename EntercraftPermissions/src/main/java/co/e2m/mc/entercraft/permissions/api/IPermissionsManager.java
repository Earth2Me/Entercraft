
package co.e2m.mc.entercraft.permissions.api;

import net.milkbowl.vault.permission.IPermission;


/**
 * A cloud-based permissions handler.
 */
public interface IPermissionsManager extends IPermission
{
	/**
	 * Enables or disables the permissions handler.
	 *
	 * @param value true to enable the permissions handler; otherwise, false
	 */
	void setEnabled(boolean value);

	/**
	 * Gets the backend in charge of storing and retrieving data.
	 *
	 * @return the database backend
	 */
	IPermissionsBackend getBackend();
}

package com.earth2me.mc.cloudpermissions;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * Provides the main entrypoint and manager for the Bukkit plugin.
 */
public final class CloudPermissionsPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		this.getCommand("cp").setExecutor(
		super.onEnable();
	}
}

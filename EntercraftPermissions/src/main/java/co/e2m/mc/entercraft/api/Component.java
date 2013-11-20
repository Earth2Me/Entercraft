
package co.e2m.mc.entercraft.api;

import co.e2m.mc.entercraft.api.IComponent;
import co.e2m.mc.entercraft.api.IComponentsPlugin;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;


/**
 * A plugin component base that receives several plugin events.
 */
@RequiredArgsConstructor
public abstract class Component implements IComponent
{
	/**
	 * {@inheritDoc}
	 */
	@Getter
	private final transient IComponentsPlugin plugin;

	/**
	 * Gets the logger for the current plugin.
	 *
	 * @return associated plugin's logger, or Bukkit logger if no plugin is specified
	 */
	protected Logger getLogger()
	{
		if (plugin != null)
		{
			return plugin.getLogger();
		}
		else
		{
			return Bukkit.getLogger();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLoad()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisable()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReload()
	{
	}
}

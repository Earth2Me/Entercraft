package co.e2m.mc.entercraft.permissions.api;

import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;


/**
 * Identifies a cached snapshot version.
 */
public abstract class CachedStore
{
	/**
	 * Used to generate cache versions.
	 */
	private static final Random random = new Random();

	/**
	 * Gets a value indicating the current cache snapshot version.
	 *
	 * @return cache version, used to ensure that the cache is up-to-date
	 */
	@Getter
	private transient long cacheVersion;

	/**
	 * Gets the permissions manager with which this store is associated.
	 *
	 * @return the permissions manager
	 */
	@Getter(AccessLevel.PROTECTED)
	private final transient IPermissionsManager manager;

	/**
	 * Instantiates a new cached datastore.
	 * 
	 * @param manager the permissions manager with which this store is associated
	 */
	protected CachedStore(IPermissionsManager manager)
	{
		this.manager = manager;
	}

	/**
	 * Gets the backend in charge of storing and retrieving data.
	 *
	 * @return the database backend
	 */
	protected IPermissionsBackend getBackend()
	{
		return manager.getBackend();
	}

	/**
	 * Changes the cache version.
	 */
	public final void touch()
	{
		cacheVersion = random.nextLong();
	}
}

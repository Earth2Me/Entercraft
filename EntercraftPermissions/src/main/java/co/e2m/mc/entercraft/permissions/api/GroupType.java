package co.e2m.mc.entercraft.permissions.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Types of groups.
 */
@RequiredArgsConstructor
public enum GroupType
{
	/**
	 * A normal group.
	 */
	NORMAL("G"),
	/**
	 * A group for a single user.
	 */
	USER("U");

	/**
	 * Gets the ID of the group type.
	 *
	 * @return the ID of the group type, typically a single character
	 */
	@Getter
	private final String id;

	/**
	 * {@inheritDoc}
	 *
	 * @return the ID of the group type, typically a single character
	 */
	@Override
	public String toString()
	{
		return id;
	}

	/**
	 * Gets a group type by ID.
	 *
	 * @param id the ID of the group type
	 * @param defaultType the default value to return if no type is matched
	 * @return a matching group type, or the default if there is no match
	 */
	public static GroupType parse(final String id, final GroupType defaultType)
	{
		for (final GroupType type : values())
		{
			if (type.id.equals(id))
			{
				return type;
			}
		}

		return defaultType;
	}
}

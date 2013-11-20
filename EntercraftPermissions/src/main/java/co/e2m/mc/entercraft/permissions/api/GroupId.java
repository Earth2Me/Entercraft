
package co.e2m.mc.entercraft.permissions.api;

import co.e2m.mc.entercraft.i18n.I18n;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Identifies a single permissions group.
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = {
	"name",
	"world",
	"type",
})
public class GroupId
{
	/**
	 * Used to split group names into sections.
	 */
	private static final Pattern splitRegex = Pattern.compile("/");

	/**
	 * Gets the relatively unique name of the group.
	 *
	 * @return the name of the group
	 */
	@Getter
	private final String name;

	/**
	 * Gets the world for which the group is applicable.
	 *
	 * @return the group's world
	 */
	@Getter
	private final String world;

	/**
	 * Gets the type of group.
	 *
	 * @return the group's type
	 */
	@Getter
	private final GroupType type;

	/**
	 * Cache for {@link #getCanonicalName()}.
	 */
	private transient String canonicalNameCache;

	/**
	 * Instantiates a new group identifier with the provided raw arguments.
	 *
	 * @param world world ID, to be interned; case-sensitive
	 * @param type group type
	 * @param name group name, to be converted to lowercase and interned
	 */
	public GroupId(final String world, final GroupType type, final String name)
	{
		assert name != null;

		if (name.contains("/"))
		{
			final String[] tokens = splitRegex.split(name, 3);
			if (tokens.length >= 3)
			{
				this.world = tokens[0].intern();
				this.type = GroupType.parse(tokens[1], GroupType.NORMAL);
				this.name = tokens[3].toLowerCase(I18n.INVARIANT_LOCALE);
				return;
			}
		}

		assert world != null;
		assert type != null;

		this.world = world.intern();
		this.type = type;
		this.name = name.toLowerCase(I18n.INVARIANT_LOCALE).intern();
	}

	/**
	 * Gets a server-unique full name for the group.
	 *
	 * @return the group's canonical name
	 */
	public String getCanonicalName()
	{
		if (canonicalNameCache == null)
		{
			canonicalNameCache = String.format("%s/%s/%s", name, world, type).intern();
		}

		return canonicalNameCache;
	}

	/**
	 * @{inheritDoc}
	 *
	 * @return the group's canonical name
	 */
	@Override
	public String toString()
	{
		return getCanonicalName();
	}
}

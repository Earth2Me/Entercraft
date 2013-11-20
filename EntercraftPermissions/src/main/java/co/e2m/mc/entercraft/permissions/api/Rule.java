package co.e2m.mc.entercraft.permissions.api;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The result of applying a permission tree to a single permission.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public final class Rule implements Comparable<Rule>
{
	/**
	 * Represents an ineffective rule.
	 */
	public static final Rule NONE = new Rule(NodeEffect.NONE, 0);

	/**
	 * Gets the applicable effect of the rule.
	 *
	 * @return the effect of the rule
	 */
	private final NodeEffect effect;

	/**
	 * Gets the priority of the rule, where a higher number indicates a higher priority.
	 *
	 * @return the priority of the rule
	 */
	private final int priority;

	/**
	 * Determines which rule takes precedence.
	 *
	 * @param o other rule
	 * @return a positive number if the current rule takes precedence; a negative number if the other rule takes precedence; 0 if they are
	 * identical
	 */
	@Override
	public int compareTo(final Rule o)
	{
		assert o != null;

		if (equals(o))
		{
			return 0;
		}

		if (equals(NONE))
		{
			return -1;
		}
		if (Objects.equals(o, NONE))
		{
			return 1;
		}

		if (priority != o.priority)
		{
			return priority - o.priority;
		}

		return effect.compareTo(o.effect);
	}

	/**
	 * Determines the effective rule from a list of rules.
	 *
	 * @param rules list of rules to assess
	 * @return the dominant rule from the list
	 */
	public static Rule getEffective(final List<Rule> rules)
	{
		if (rules == null || rules.isEmpty())
		{
			return Rule.NONE;
		}

		Collections.sort(rules);
		return rules.get(rules.size() - 1);
	}
}

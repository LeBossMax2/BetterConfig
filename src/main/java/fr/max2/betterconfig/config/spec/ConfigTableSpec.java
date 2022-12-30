package fr.max2.betterconfig.config.spec;

import java.util.List;

import fr.max2.betterconfig.config.ConfigTableKey;

/**
 * Represents the specification for a table in a configuration
 */
public final record ConfigTableSpec
(
	/** The list of valid entries in this table */
	List<Entry> entries
)
implements ConfigSpec
{
	public ConfigTableSpec(List<Entry> entries)
	{
		this.entries = List.copyOf(entries);
	}

	public static record Entry
	(
		/** The name of the entry */
		ConfigTableKey key,
		/** The specification of the possible values for the entry */
		ConfigSpec node
	)
	{ }
}

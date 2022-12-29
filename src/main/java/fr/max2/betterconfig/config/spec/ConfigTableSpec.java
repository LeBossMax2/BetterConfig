package fr.max2.betterconfig.config.spec;

import fr.max2.betterconfig.config.ConfigIdentifier;

public final class ConfigTableSpec implements ConfigSpec
{
	private final java.util.List<Entry> entries;

	public ConfigTableSpec(java.util.List<Entry> entries)
	{
		this.entries = java.util.List.copyOf(entries);
	}

	public java.util.List<Entry> entries()
	{
		return this.entries;
	}

	public static record Entry
	(
		ConfigIdentifier key,
		ConfigSpec node
	)
	{ }
}

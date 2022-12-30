package fr.max2.betterconfig.config.spec;

import java.util.List;

import fr.max2.betterconfig.config.ConfigTableKey;

public final class ConfigTableSpec implements ConfigSpec
{
	private final List<Entry> entries;

	public ConfigTableSpec(List<Entry> entries)
	{
		this.entries = List.copyOf(entries);
	}

	public List<Entry> entries()
	{
		return this.entries;
	}

	public static record Entry
	(
		ConfigTableKey key,
		ConfigSpec node
	)
	{ }
}

package fr.max2.betterconfig.config.value;

import java.util.List;

import fr.max2.betterconfig.config.ConfigTableKey;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;

/**
 * A node containing a table in a configuration tree
 */
public final class ConfigTable implements ConfigNode
{
	private final ConfigTableSpec spec;
	private final List<Entry> entryValues;

	private ConfigTable(ConfigTableSpec spec)
	{
		this.spec = spec;
		this.entryValues = spec.entries().stream().map(entry ->
			new Entry(entry.key(), ConfigNode.make(entry.node()))
		).toList();
	}

	/**
	 * Builds a {@code ConfigTable} for the given specification
	 * @param spec the specification of the node to create
	 * @return the newly created table node
	 */
	public static ConfigTable make(ConfigTableSpec spec)
	{
		return new ConfigTable(spec);
	}

	@Override
	public ConfigTableSpec getSpec()
	{
		return this.spec;
	}

	@Override
	public Object getValue()
	{
		return null; // TODO [#5] table getValue
	}

	/**
	 * Returns the list of entries in this table
	 */
	public List<Entry> entries()
	{
		return this.entryValues;
	}

	@Override
	public void setAsInitialValue()
	{
		this.entryValues.forEach(entry -> entry.node().setAsInitialValue());
	}

	@Override
	public void undoChanges()
	{
		this.entryValues.forEach(entry -> entry.node().undoChanges());
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("{ ");

		boolean fist = true;
		for (var entry : this.entryValues)
		{
			if (!fist)
			{
				builder.append(", ");
			}
			fist = false;
			builder.append(entry.key());
			builder.append(":");
			builder.append(entry.node().toString());
		}

		builder.append(" }");
		return builder.toString();
	}

	public static record Entry
	(
		/** The name of the entry */
		ConfigTableKey key,
		/** The configuration value of the entry */
		ConfigNode node
	)
	{ }
}

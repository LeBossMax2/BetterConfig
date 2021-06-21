package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;

public class ConfigTableEntry
{
	private final ConfigTableEntrySpec spec;
	private final ConfigNode<?> node;

	public ConfigTableEntry(ConfigTableEntrySpec spec, ConfigNode<?> node)
	{
		this.spec = spec;
		this.node = node;
	}
	
	public ConfigTableEntrySpec getSpec()
	{
		return this.spec;
	}
	
	public ConfigNode<?> getNode()
	{
		return this.node;
	}
}

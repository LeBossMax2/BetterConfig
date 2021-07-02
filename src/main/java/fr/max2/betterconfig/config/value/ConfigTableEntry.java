package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;

public class ConfigTableEntry
{
	private final ConfigTableEntrySpec spec;
	private final IConfigNode<?> node;

	public ConfigTableEntry(ConfigTableEntrySpec spec, IConfigNode<?> node)
	{
		this.spec = spec;
		this.node = node;
	}
	
	public ConfigTableEntrySpec getSpec()
	{
		return this.spec;
	}
	
	public IConfigNode<?> getNode()
	{
		return this.node;
	}
}

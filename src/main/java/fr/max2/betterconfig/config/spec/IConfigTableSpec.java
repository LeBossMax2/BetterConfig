package fr.max2.betterconfig.config.spec;

import java.util.List;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.ConfigIdentifier;

public interface IConfigTableSpec extends IConfigSpecNode
{
	@Override
	default Class<UnmodifiableConfig> getValueClass()
	{
		return UnmodifiableConfig.class;
	}
	
	@Override
	UnmodifiableConfig getDefaultValue();
	
	List<Entry> getEntrySpecs();
	
	public static record Entry
	(
		ConfigIdentifier key,
		ConfigSpecNode node
	)
	{ }
}

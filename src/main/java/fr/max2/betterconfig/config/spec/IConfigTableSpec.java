package fr.max2.betterconfig.config.spec;

import java.util.List;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

public interface IConfigTableSpec extends IConfigSpecNode
{
	@Override
	default Class<UnmodifiableConfig> getValueClass()
	{
		return UnmodifiableConfig.class;
	}
	
	@Override
	UnmodifiableConfig getDefaultValue();
	
	List<ConfigTableEntrySpec> getEntrySpecs();
}

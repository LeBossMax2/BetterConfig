package fr.max2.betterconfig.config.spec;

import java.util.List;

public interface IConfigListSpec extends IConfigSpecNode
{
	@Override
	default Class<? super List<?>> getValueClass()
	{
		return List.class;
	}

	@Override
	List<?> getDefaultValue();

	ConfigSpec getElementSpec();
}

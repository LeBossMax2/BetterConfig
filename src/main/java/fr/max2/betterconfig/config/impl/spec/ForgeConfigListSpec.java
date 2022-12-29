package fr.max2.betterconfig.config.impl.spec;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.IConfigListSpec;

public class ForgeConfigListSpec implements IConfigListSpec
{
	private final ConfigSpec elementSpec;

	public ForgeConfigListSpec(ConfigSpec elementSpec)
	{
		this.elementSpec = elementSpec;
	}

	@Override
	public ConfigSpec getElementSpec()
	{
		return this.elementSpec;
	}

	@Override
	public List<?> getDefaultValue()
	{
		return new ArrayList<>();
	}

}

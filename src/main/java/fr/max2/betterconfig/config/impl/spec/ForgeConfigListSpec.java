package fr.max2.betterconfig.config.impl.spec;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.spec.ConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigListSpec;

public class ForgeConfigListSpec implements IConfigListSpec
{
	private final ConfigSpecNode elementSpec;

	public ForgeConfigListSpec(ConfigSpecNode elementSpec)
	{
		this.elementSpec = elementSpec;
	}

	@Override
	public ConfigSpecNode getElementSpec()
	{
		return this.elementSpec;
	}

	@Override
	public List<?> getDefaultValue()
	{
		return new ArrayList<>();
	}

}

package fr.max2.betterconfig.config.impl.spec;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;

public class ForgeConfigListSpec<T> implements IConfigListSpec
{
	private final IConfigSpecNode elementSpec;
	
	public ForgeConfigListSpec(IConfigSpecNode elementSpec)
	{
		this.elementSpec = elementSpec;
	}

	@Override
	public IConfigSpecNode getElementSpec()
	{
		return this.elementSpec;
	}

	@Override
	public List<T> getDefaultValue()
	{
		return new ArrayList<>();
	}
	
}

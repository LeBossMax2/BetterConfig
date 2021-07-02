package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;

public class ForgeConfigListSpec implements IConfigListSpec
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
	
}

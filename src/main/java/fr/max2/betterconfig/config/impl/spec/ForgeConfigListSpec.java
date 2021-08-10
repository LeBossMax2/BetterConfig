package fr.max2.betterconfig.config.impl.spec;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;

public class ForgeConfigListSpec<T> implements IConfigListSpec<T>
{
	private final IConfigSpecNode<T> elementSpec;
	
	public ForgeConfigListSpec(IConfigSpecNode<T> elementSpec)
	{
		this.elementSpec = elementSpec;
	}

	@Override
	public IConfigSpecNode<T> getElementSpec()
	{
		return this.elementSpec;
	}

	@Override
	public List<T> getDefaultValue()
	{
		return new ArrayList<>();
	}
	
}

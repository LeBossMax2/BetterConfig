package fr.max2.betterconfig.config.impl.value;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.value.IConfigNode;

public abstract class ForgeConfigNode<Spec extends IConfigSpecNode> implements IConfigNode
{
	private final Spec spec;
	
	public ForgeConfigNode(Spec spec)
	{
		this.spec = spec;
	}
	
	@Override
	public Spec getSpec()
	{
		return this.spec;
	}
	
	protected abstract Object getCurrentValue();
}

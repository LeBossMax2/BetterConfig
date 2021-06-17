package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.ConfigSpecNode;

public abstract class ConfigNode<Spec extends ConfigSpecNode>
{
	private final Spec spec;

	public ConfigNode(Spec spec)
	{
		this.spec = spec;
	}
	
	public Spec getSpec()
	{
		return spec;
	}
	
	public <R> R exploreNode(IConfigValueVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	public abstract <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param);
}

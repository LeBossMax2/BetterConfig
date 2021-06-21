package fr.max2.betterconfig.config.spec;

public abstract class ConfigSpecNode
{
	public <R> R exploreNode(IConfigSpecVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	public abstract <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param);
}

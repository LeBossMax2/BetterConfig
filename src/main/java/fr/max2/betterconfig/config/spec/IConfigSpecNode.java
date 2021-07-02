package fr.max2.betterconfig.config.spec;

public interface IConfigSpecNode
{
	default <R> R exploreNode(IConfigSpecVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	<P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param);
}

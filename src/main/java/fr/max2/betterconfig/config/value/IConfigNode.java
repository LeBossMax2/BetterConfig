package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;

public interface IConfigNode<Spec extends IConfigSpecNode>
{
	Spec getSpec();
	
	default <R> R exploreNode(IConfigValueVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	<P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param);
}

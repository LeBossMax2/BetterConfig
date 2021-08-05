package fr.max2.betterconfig.config.spec;

import java.util.List;

public interface IConfigListSpec<T> extends IConfigSpecNode<List<T>>
{
	@Override
	default Class<? super List<T>> getValueClass()
	{
		return List.class;
	}
	
	IConfigSpecNode<T> getElementSpec();
	
	@Override
	default <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitList(this, param);
	}
}

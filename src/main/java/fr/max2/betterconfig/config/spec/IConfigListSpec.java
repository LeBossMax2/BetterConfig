package fr.max2.betterconfig.config.spec;

public interface IConfigListSpec extends IConfigSpecNode
{
	IConfigSpecNode getElementSpec();
	
	@Override
	default <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitList(this, param);
	}
}

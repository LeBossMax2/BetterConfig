package fr.max2.betterconfig.config.spec;

import java.util.List;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

public interface IConfigTableSpec extends IConfigSpecNode<UnmodifiableConfig>
{
	@Override
	default Class<UnmodifiableConfig> getValueClass()
	{
		return UnmodifiableConfig.class;
	}
	
	List<ConfigTableEntrySpec> getEntrySpecs();
	
	@Override
	default <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
	
}

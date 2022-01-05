package fr.max2.betterconfig.config.spec;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

public interface IConfigTableSpec extends IConfigSpecNode<UnmodifiableConfig>
{
	@Override
	default Class<UnmodifiableConfig> getValueClass()
	{
		return UnmodifiableConfig.class;
	}
	
	Map<String, ConfigTableEntrySpec> getSpecMap();
	
	default <R> Stream<R> exploreEntries(BiFunction<String, ConfigTableEntrySpec, R> visitor)
	{
		return this.getSpecMap().entrySet().stream().map(entry -> visitor.apply(entry.getKey(), entry.getValue()));
	}
	
	@Override
	default <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
	
}

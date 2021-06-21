package fr.max2.betterconfig.config.spec;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public abstract class ConfigTableSpec extends ConfigSpecNode
{
	public abstract Map<String, ConfigTableEntrySpec> getSpecMap();
	
	public <R> Stream<R> exploreEntries(BiFunction<String, ConfigTableEntrySpec, R> visitor)
	{
		return this.getSpecMap().entrySet().stream().map(entry -> visitor.apply(entry.getKey(), entry.getValue()));
	}
	
	@Override
	public <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
	
}

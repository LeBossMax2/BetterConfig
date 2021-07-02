package fr.max2.betterconfig.config.value;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;

public interface IConfigTable extends IConfigNode<IConfigTableSpec>
{
	Map<String, ConfigTableEntry> getValueMap();
	
	default <R> Stream<R> exploreEntries(BiFunction<ConfigTableEntrySpec, IConfigNode<?>, R> visitor)
	{
		return this.getValueMap().entrySet().stream().map(entry -> visitor.apply(entry.getValue().getSpec(), entry.getValue().getNode()));
	}
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
}

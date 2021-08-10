package fr.max2.betterconfig.config.value;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.spec.IConfigTableSpec;

public interface IConfigTable extends IConfigNode<UnmodifiableConfig>
{
	Map<String, ? extends IConfigNode<?>> getValueMap();
	
	@Override
	IConfigTableSpec getSpec();
	
	default <R> Stream<R> exploreEntries(Function<IConfigNode<?>, R> visitor)
	{
		return this.getValueMap().entrySet().stream().map(entry -> visitor.apply(entry.getValue()));
	}
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
}

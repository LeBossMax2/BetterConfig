package fr.max2.betterconfig.config.value;

import java.util.function.Function;
import java.util.stream.Stream;

import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.util.property.list.IReadableList;

public interface IConfigList extends IConfigNode
{
	IReadableList<IConfigNode> getValueList();
	
	void removeValueAt(int index);
	
	IConfigNode addValue(int index);
	
	@Override
	IConfigListSpec getSpec();
	
	default <R> Stream<R> exploreElements(Function<IConfigNode, R> visitor)
	{
		return this.getValueList().stream().map(elem -> visitor.apply(elem));
	}
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitList(this, param);
	}
}

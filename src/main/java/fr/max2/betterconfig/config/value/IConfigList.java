package fr.max2.betterconfig.config.value;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.max2.betterconfig.config.spec.IConfigListSpec;

public interface IConfigList extends IConfigNode<IConfigListSpec>
{
	List<? extends IConfigNode<?>> getValueList();
	
	void removeValueAt(int index);
	
	IConfigNode<?> addValue(int index);
	
	default <R> Stream<R> exploreElements(Function<IConfigNode<?>, R> visitor)
	{
		return this.getValueList().stream().map(elem -> visitor.apply(elem));
	}
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitList(this, param);
	}
}

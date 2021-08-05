package fr.max2.betterconfig.config.value;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.max2.betterconfig.config.spec.IConfigListSpec;

public interface IConfigList<T> extends IConfigNode<List<T>>
{
	List<? extends IConfigNode<T>> getValueList();
	
	void removeValueAt(int index);
	
	IConfigNode<T> addValue(int index);
	
	@Override
	IConfigListSpec<T> getSpec();
	
	default <R> Stream<R> exploreElements(Function<IConfigNode<T>, R> visitor)
	{
		return this.getValueList().stream().map(elem -> visitor.apply(elem));
	}
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitList(this, param);
	}
}

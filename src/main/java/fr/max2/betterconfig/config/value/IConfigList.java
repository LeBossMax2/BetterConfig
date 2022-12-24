package fr.max2.betterconfig.config.value;

import java.util.function.Function;
import java.util.stream.Stream;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.util.property.list.IReadableList;

public interface IConfigList extends IConfigNode
{
	IReadableList<Entry> getValueList();
	
	void removeValueAt(int index);
	
	Entry addValue(int index);
	
	@Override
	IConfigListSpec getSpec();
	
	default <R> Stream<R> exploreElements(Function<Entry, R> visitor)
	{
		return this.getValueList().stream().map(entry -> visitor.apply(entry));
	}

	public static record Entry
	(
		IConfigName key,
		IConfigNode node
	)
	{ }
}

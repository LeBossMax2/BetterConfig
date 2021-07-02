package fr.max2.betterconfig.util;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

public class MappedListView<U, V> extends AbstractList<V> 
{
	private final List<U> parent;
	private final Function<U, V> mapper;
	
	public MappedListView(List<U> parent, Function<U, V> mapper)
	{
		this.parent = parent;
		this.mapper = mapper;
	}

	@Override
	public int size()
	{
		return this.parent.size();
	}

	@Override
	public V get(int index)
	{
		return this.mapper.apply(this.parent.get(index));
	}
}

package fr.max2.betterconfig.util;

import java.util.Iterator;
import java.util.function.Function;

public class MappedIterator<U, V> implements Iterator<V>
{
	private final Iterator<U> parent;
	private final Function<U, V> mapper;
	
	public MappedIterator(Iterator<U> parent, Function<U, V> mapper)
	{
		this.parent = parent;
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext()
	{
		return this.parent.hasNext();
	}

	@Override
	public V next()
	{
		return this.mapper.apply(this.parent.next());
	}
}

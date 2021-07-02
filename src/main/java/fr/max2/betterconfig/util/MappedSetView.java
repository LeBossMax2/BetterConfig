package fr.max2.betterconfig.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

public class MappedSetView<U, V> extends AbstractSet<V>
{
	private final Set<U> parent;
	private final Function<U, V> mapper;

	public MappedSetView(Set<U> parent, Function<U, V> mapper)
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
	public boolean isEmpty()
	{
		return this.parent.isEmpty();
	}

	@Override
	public Iterator<V> iterator()
	{
		return new MappedIterator<>(this.parent.iterator(), this.mapper);
	}
}

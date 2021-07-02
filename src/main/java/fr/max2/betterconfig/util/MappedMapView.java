package fr.max2.betterconfig.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class MappedMapView<K, U, V> extends AbstractMap<K, V>
{
	private final Map<K, U> parent;
	private final BiFunction<K, U, V> mapper;
	private transient Set<Entry<K, V>> entrySet;

	public MappedMapView(Map<K, U> parent, BiFunction<K, U, V> mapper)
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
	public boolean containsKey(Object key)
	{
		return this.parent.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key)
	{
		return this.mapper.apply((K)key, this.parent.get(key));
	}

	@Override
	public Set<Entry<K, V>> entrySet()
	{
		if (this.entrySet == null)
		{
			this.entrySet = new MappedSetView<>(this.parent.entrySet(), entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), this.mapper.apply(entry.getKey(), entry.getValue())));
		}
		return this.entrySet;
	}
}

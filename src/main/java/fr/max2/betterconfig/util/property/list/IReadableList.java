package fr.max2.betterconfig.util.property.list;

import java.util.List;
import java.util.function.Function;

import fr.max2.betterconfig.util.property.IReadableProperty;

public interface IReadableList<T> extends List<T>
{
	List<? extends IReadableProperty<? extends T>> getElementProperties();
	List<? extends IIndexedProperty<? extends T>> getIndexedProperties();
	
	default <R> IReadableList<R> derived(IIndexedFunc<T, R> mapper)
	{
		return new DerivedList<>(this, mapper);
	}
	
	default <R> IReadableList<R> derived(Function<T, R> mapper)
	{
		return this.derived((index, val) -> mapper.apply(val));
	}

	void onChanged(IListListener<? super T> listener);
	void removeOnChangedListener(IListListener<? super T> listener);
}

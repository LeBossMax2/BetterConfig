package fr.max2.betterconfig.util.property.list;

import java.util.List;
import java.util.function.Function;

import fr.max2.betterconfig.util.IEvent;
import fr.max2.betterconfig.util.property.IReadableProperty;

public interface IReadableList<T> extends List<T>
{
	List<? extends IReadableProperty<? extends T>> getElementProperties();

	default <R> DerivedList<?, R> derived(Function<T, R> mapper)
	{
		return new DerivedList<>(this, mapper);
	}

	IEvent<? super IListListener<? super T>> onChanged();
}

package fr.max2.betterconfig.util.property;

import fr.max2.betterconfig.util.IEvent;

public interface IReadableProperty<T>
{
	T getValue();

	IEvent<IListener<? super T>> onChanged();
}

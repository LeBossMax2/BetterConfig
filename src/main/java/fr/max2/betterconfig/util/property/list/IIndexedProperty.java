package fr.max2.betterconfig.util.property.list;

import fr.max2.betterconfig.util.property.IReadableProperty;

public interface IIndexedProperty<T> extends IReadableProperty<T>
{
	int getIndex();
}

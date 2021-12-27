package fr.max2.betterconfig.util.property.list;


public interface IListListener<T>
{
	void onElementAdded(int index, T newValue);
	void onElementRemoved(int index, T oldValue);
}

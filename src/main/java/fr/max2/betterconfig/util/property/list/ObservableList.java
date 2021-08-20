package fr.max2.betterconfig.util.property.list;

import java.util.List;

public class ObservableList<T> extends ListBase<T, ListBase.PropertyBase<T>>
{
	public ObservableList()
	{ }
	
	public ObservableList(List<T> initialValues)
	{
		for (T value : initialValues)
		{
			this.parent.add(new ListBase.PropertyBase<>(value));
		}
	}
	
	@Override
	public T set(int index, T element)
	{
		return this.parent.get(index).setValue(element);
	}
	
	@Override
	public void add(int index, T element)
	{
		this.addElement(index, new ListBase.PropertyBase<>(element));
	}
	
	@Override
	public T remove(int index)
	{
		return this.removeElement(index).get();
	}
}

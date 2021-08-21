package fr.max2.betterconfig.util.property.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.IListener;
import fr.max2.betterconfig.util.property.IReadableProperty;

public abstract class ListBase<T, P extends IReadableProperty<T>> extends MappedListView<P, T> implements IReadableList<T>
{
	protected final Set<IListListener<? super T>> listeners = new HashSet<>();
	protected final List<IReadableProperty<T>> elementProperties;
	
	protected ListBase()
	{
		this(new ArrayList<>());
	}
	
	protected ListBase(List<P> properties)
	{
		super(properties, IReadableProperty::getValue);
		this.elementProperties = Collections.unmodifiableList(this.parent);
	}

	@Override
	public List<IReadableProperty<T>> getElementProperties()
	{
		return this.elementProperties;
	}
	
	@Override
	public void onChanged(IListListener<? super T> listener)
	{
		this.listeners.add(listener);
	}
	
	@Override
	public void removeOnChangedListener(IListListener<? super T> listener)
	{
		this.listeners.remove(listener);
	}
	
	protected void addElement(int index, P newProperty)
	{
		ListBase.this.parent.add(index, newProperty);
		listeners.forEach(l -> l.onElementAdded(index, newProperty.getValue()));
	}

	protected P removeElement(int index)
	{
		P property = ListBase.this.parent.remove(index);
		listeners.forEach(l -> l.onElementRemoved(index));
		return property;
	}
	
	protected static class PropertyBase<T> implements IReadableProperty<T>
	{
		protected final Set<IListener<T>> listeners = new HashSet<>();
		protected T currentValue;

		protected PropertyBase(T initialValue)
		{
			this.currentValue = initialValue;
		}

		@Override
		public T getValue()
		{
			return this.currentValue;
		}

		@Override
		public void onChanged(IListener<T> listener)
		{
			this.listeners.add(listener);
		}
		
		@Override
		public void removeOnChangedListener(IListener<? super T> listener)
		{
			this.listeners.remove(listener);
		}
		
		protected T setValue(T newValue)
		{
			T oldValue = this.currentValue;
			this.currentValue = newValue;
			this.listeners.forEach(l -> l.onValueChanged(newValue));
			return oldValue;
		}
	}
}

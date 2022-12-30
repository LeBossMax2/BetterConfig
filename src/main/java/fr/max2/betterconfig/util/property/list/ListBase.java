package fr.max2.betterconfig.util.property.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.max2.betterconfig.util.EventDispatcher;
import fr.max2.betterconfig.util.IEvent;
import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.IListener;
import fr.max2.betterconfig.util.property.IReadableProperty;

public abstract class ListBase<T, P extends IReadableProperty<T>> extends MappedListView<P, T> implements IReadableList<T>
{
	protected final EventDispatcher<IListListener<? super T>> onChanged = EventDispatcher.unordered();
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
	public IEvent<IListListener<? super T>> onChanged()
	{
		return this.onChanged;
	}

	protected void addElement(int index, P newProperty)
	{
		this.parent.add(index, newProperty);
		this.onChanged.dispatch(l -> l.onElementAdded(index, newProperty.getValue()));
	}

	protected P removeElement(int index)
	{
		P property = ListBase.this.parent.remove(index);

		this.onChanged.dispatch(l -> l.onElementRemoved(index, property.getValue()));
		return property;
	}

	protected static class PropertyBase<T> implements IReadableProperty<T>
	{
		protected final EventDispatcher<IListener<? super T>> onChanged = EventDispatcher.unordered();
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
		public IEvent<IListener<? super T>> onChanged()
		{
			return this.onChanged;
		}

		protected T setValue(T newValue)
		{
			T oldValue = this.currentValue;
			this.currentValue = newValue;
			this.onChanged.dispatch(l -> l.onValueChanged(newValue));
			return oldValue;
		}
	}
}

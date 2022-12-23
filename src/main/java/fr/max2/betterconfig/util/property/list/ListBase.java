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
	protected List<IndexedPropertyBase<T>> indexedPropertiesMut;
	protected List<IIndexedProperty<T>> indexedProperties;

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
	public List<IIndexedProperty<T>> getIndexedProperties()
	{
		if (this.indexedProperties == null)
		{
			this.indexedPropertiesMut = new ArrayList<>();
			for (int i = 0; i < this.size(); i++)
			{
				this.indexedPropertiesMut.add(i, new IndexedPropertyBase<>(i, ListBase.this.getElementProperties().get(i)));
			}
			this.indexedProperties = Collections.unmodifiableList(this.indexedPropertiesMut);
		}
		return this.indexedProperties;
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

		if (this.indexedPropertiesMut != null)
		{
			this.indexedPropertiesMut.add(index, new IndexedPropertyBase<>(index, ListBase.this.getElementProperties().get(index)));

			for (int i = index + 1; i < this.indexedPropertiesMut.size(); i++)
			{
				this.indexedPropertiesMut.get(i).updateIndex(i);
			}
		}

		this.listeners.forEach(l -> l.onElementAdded(index, newProperty.getValue()));
	}

	protected P removeElement(int index)
	{
		P property = ListBase.this.parent.remove(index);

		if (this.indexedPropertiesMut != null)
		{
			this.indexedPropertiesMut.remove(index);

			for (int i = index; i < this.indexedPropertiesMut.size(); i++)
			{
				this.indexedPropertiesMut.get(i).updateIndex(i);
			}
		}

		this.listeners.forEach(l -> l.onElementRemoved(index, property.getValue()));
		return property;
	}

	protected static class PropertyBase<T> implements IReadableProperty<T>
	{
		protected final Set<IListener<? super T>> listeners = new HashSet<>();
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
		public void onChanged(IListener<? super T> listener)
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

	protected static class IndexedPropertyBase<T> implements IIndexedProperty<T>
	{
		private int index;
		private final IReadableProperty<T> parent;

		public IndexedPropertyBase(int index, IReadableProperty<T> parent)
		{
			this.index = index;
			this.parent = parent;
		}

		@Override
		public T getValue()
		{
			return this.parent.getValue();
		}

		@Override
		public void onChanged(IListener<? super T> listener)
		{
			this.parent.onChanged(listener);
		}

		@Override
		public void removeOnChangedListener(IListener<? super T> listener)
		{
			this.parent.removeOnChangedListener(listener);
		}

		public void updateIndex(int index)
		{
			this.index = index;
		}

		@Override
		public int getIndex()
		{
			return this.index;
		}
	}
}

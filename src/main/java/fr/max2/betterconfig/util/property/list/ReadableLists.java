package fr.max2.betterconfig.util.property.list;

import java.util.AbstractList;
import java.util.List;

import fr.max2.betterconfig.util.property.IReadableProperty;

public enum ReadableLists
{
	;
	
	public static <T> IReadableList<T> unmodifiableList(IReadableList<? extends T> base)
	{
		return new UnmodifiableList<>(base);
	}
	
	public static class UnmodifiableList<T> extends AbstractList<T> implements IReadableList<T>
	{
		private IReadableList<? extends T> parent;
		
		public UnmodifiableList(IReadableList<? extends T> parent)
		{
			this.parent = parent;
		}

		@Override
		public List<? extends IReadableProperty<? extends T>> getElementProperties()
		{
			return this.parent.getElementProperties();
		}
		
		@Override
		public List<? extends IIndexedProperty<? extends T>> getIndexedProperties()
		{
			return this.parent.getIndexedProperties();
		}

		@Override
		public void onChanged(IListListener<? super T> listener)
		{
			this.parent.onChanged(listener);
		}
		
		@Override
		public void removeOnChangedListener(IListListener<? super T> listener)
		{
			this.parent.removeOnChangedListener(listener);
		}

		@Override
		public T get(int index)
		{
			return this.parent.get(index);
		}

		@Override
		public int size()
		{
			return this.parent.size();
		}
	}
}

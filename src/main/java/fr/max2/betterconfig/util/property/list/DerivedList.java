package fr.max2.betterconfig.util.property.list;

import fr.max2.betterconfig.util.property.IReadableProperty;

public class DerivedList<T, R> extends ListBase<R, IReadableProperty<R>>
{
	private final IIndexedFunc<? super T, R> derivationMapper;

	public DerivedList(IReadableList<T> parent, IIndexedFunc<? super T, R> mapper)
	{
		this.derivationMapper = mapper;
		
		int i = 0;
		for (IReadableProperty<? extends T> property : parent.getElementProperties())
		{
			this.parent.add(new DerivedProperty(i, property));
			i++;
		}
		
		parent.onChanged(new IListListener<T>()
		{
			@Override
			public void onElementAdded(int index, T newValue)
			{
				DerivedList.this.addElement(index, new DerivedProperty(index, parent.getElementProperties().get(index)));
			}

			@Override
			public void onElementRemoved(int index)
			{
				DerivedList.this.removeElement(index);
			}
		});
	}
	
	private class DerivedProperty extends ListBase.PropertyBase<R>
	{
		public DerivedProperty(int index, IReadableProperty<? extends T> baseProperty)
		{
			super(derivationMapper.apply(index, baseProperty.get()));
			baseProperty.onChanged(newVal -> this.setValue(derivationMapper.apply(index, newVal)));
		}
	}
}

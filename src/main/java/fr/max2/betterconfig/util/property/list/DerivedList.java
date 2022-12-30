package fr.max2.betterconfig.util.property.list;

import java.util.function.Function;

import fr.max2.betterconfig.util.property.IReadableProperty;

public class DerivedList<T, R> extends ListBase<R, IReadableProperty<R>>
{
	private final Function<? super T, R> derivationMapper;

	public DerivedList(IReadableList<T> parent, Function<? super T, R> mapper)
	{
		this.derivationMapper = mapper;

		for (IReadableProperty<? extends T> property : parent.getElementProperties())
		{
			this.parent.add(new DerivedProperty(property));
		}

		parent.onChanged().add(new IListListener<T>()
		{
			@Override
			public void onElementAdded(int index, T newValue)
			{
				DerivedList.this.addElement(index, new DerivedProperty(parent.getElementProperties().get(index)));
			}

			@Override
			public void onElementRemoved(int index, T oldValue)
			{
				DerivedList.this.removeElement(index);
			}
		});
	}

	private class DerivedProperty extends ListBase.PropertyBase<R>
	{
		public DerivedProperty(IReadableProperty<? extends T> baseProperty)
		{
			super(DerivedList.this.derivationMapper.apply(baseProperty.getValue()));
			baseProperty.onChanged().add(newVal -> this.setValue(DerivedList.this.derivationMapper.apply(newVal)));
		}
	}
}

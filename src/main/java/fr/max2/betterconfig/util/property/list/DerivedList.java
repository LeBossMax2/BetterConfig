package fr.max2.betterconfig.util.property.list;

import java.util.function.Function;

import fr.max2.betterconfig.util.IEvent;
import fr.max2.betterconfig.util.property.IReadableProperty;

public class DerivedList<T, R> extends ListBase<R, DerivedList<T, R>.DerivedProperty> implements AutoCloseable
{
	private final Function<? super T, R> derivationMapper;
	private final IEvent.Guard parentGuard;

	public DerivedList(IReadableList<T> parent, Function<? super T, R> mapper)
	{
		this.derivationMapper = mapper;

		for (IReadableProperty<? extends T> property : parent.getElementProperties())
		{
			this.parent.add(new DerivedProperty(property));
		}

		this.parentGuard = parent.onChanged().add(new IListListener<T>()
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

	public class DerivedProperty extends ListBase.PropertyBase<R> implements AutoCloseable
	{
		private final IEvent.Guard baseGuard;

		private DerivedProperty(IReadableProperty<? extends T> baseProperty)
		{
			super(DerivedList.this.derivationMapper.apply(baseProperty.getValue()));
			this.baseGuard = baseProperty.onChanged().add(newVal -> this.setValue(DerivedList.this.derivationMapper.apply(newVal)));
		}

		@Override
		public void close()
		{
			this.baseGuard.close();
		}
	}

	@Override
	public void close()
	{
		this.parentGuard.close();
		this.parent.forEach(DerivedProperty::close);
	}
}

package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigSpecVisitor;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.MappedListView;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigList<T> extends ForgeConfigProperty<IConfigListSpec<T>, List<T>> implements IConfigList<T>
{
	private final ListImpl<T> list;

	public ForgeConfigList(IConfigListSpec<T> spec, Consumer<ForgeConfigProperty<?, ?>> changeListener, ConfigValue<List<T>> configValue)
	{
		super(spec, changeListener, configValue);
		this.list = new ListImpl<>(this::onValueChanged, spec);
		this.list.setCurrentValue(configValue.get());
	}

	@Override
	protected List<T> getCurrentValue()
	{
		return this.list.getCurrentValue();
	}

	@Override
	public List<? extends IConfigNode<T>> getValueList()
	{
		return this.list.getValueList();
	}
	
	@Override
	public void removeValueAt(int index)
	{
		this.list.removeValueAt(index);
	}

	@Override
	public IConfigNode<T> addValue(int index)
	{
		return this.list.addValue(index);
	}
	
	public static interface IElementBuilder<T>
	{
		ElementNode<?, T> build();
	}
	
	public static abstract class ElementNode<Spec extends IConfigSpecNode<T>, T> implements IConfigNode<T>
	{
		private final Spec spec;
		protected ListImpl<T> parent;
		
		public ElementNode(Spec spec, ListImpl<T> parent)
		{
			this.spec = spec;
			this.parent = parent;
		}
		
		@Override
		public Spec getSpec()
		{
			return this.spec;
		}
		
		protected abstract T getCurrentValue();
		
		protected abstract void setCurrentValue(T value);
	}
	
	public static class ElementPrimitive<T> extends ElementNode<IConfigPrimitiveSpec<T>, T> implements IConfigPrimitive<T>
	{
		private T currentValue;
		
		public ElementPrimitive(IConfigPrimitiveSpec<T> spec, ListImpl<T> parent)
		{
			super(spec, parent);
			this.currentValue = spec.getDefaultValue();
		}

		@Override
		public T getValue()
		{
			return this.currentValue;
		}

		@Override
		protected T getCurrentValue()
		{
			return this.getValue();
		}
		
		@Override
		protected void setCurrentValue(T value)
		{
			this.currentValue = value;
		}

		@Override
		public void setValue(T value)
		{
			this.currentValue = value;
			this.parent.onValueChanged();
		}
	}
	
	public static class ElementList<T> extends ElementNode<IConfigListSpec<T>, List<T>> implements IConfigList<T>
	{
		private final ListImpl<T> list;
		
		public ElementList(IConfigListSpec<T> spec, ListImpl<List<T>> parent)
		{
			super(spec, parent);
			this.list = new ListImpl<>(parent::onValueChanged, spec);
		}

		@Override
		public List<? extends IConfigNode<T>> getValueList()
		{
			return this.list.getValueList();
		}
		
		@Override
		public void removeValueAt(int index)
		{
			this.list.removeValueAt(index);
		}

		@Override
		public IConfigNode<T> addValue(int index)
		{
			return this.list.addValue(index);
		}
		
		@Override
		protected void setCurrentValue(List<T> value)
		{
			this.list.setCurrentValue(value);
		}

		@Override
		protected List<T> getCurrentValue()
		{
			return this.list.getCurrentValue();
		}
	}
	
	private static final class ListImpl<T>
	{
		private final Runnable changeListener;
		private final IElementBuilder<T> elementBuilder;
		private final List<ElementNode<?, T>> valueList;
		private final List<IConfigNode<T>> valueListView;
		private final List<T> currentValue;

		public ListImpl(Runnable changeListener, IConfigListSpec<T> specs)
		{
			this.changeListener = changeListener;
			this.elementBuilder = specs.getElementSpec().exploreNode(new ElementBuilderChooser<>(), this);

			this.valueList = new ArrayList<>();
			this.valueListView = Collections.unmodifiableList(this.valueList);
			this.currentValue = new MappedListView<>(this.valueList, elem -> elem.getCurrentValue());
		}
		
		protected void setCurrentValue(List<T> value)
		{
			this.valueList.clear();
			
			if (value != null)
			{
				for (int i = 0; i < value.size(); i++)
				{
					ElementNode<?, T> elem = this.elementBuilder.build();
					elem.setCurrentValue(value.get(i));
					this.valueList.add(elem);
				}
			}
		}

		protected List<T> getCurrentValue()
		{
			return this.currentValue;
		}

		public List<? extends IConfigNode<T>> getValueList()
		{
			return this.valueListView;
		}

		public void removeValueAt(int index)
		{
			this.valueList.remove(index);
			this.onValueChanged();
		}

		public IConfigNode<T> addValue(int index)
		{
			Preconditions.checkPositionIndex(index, this.valueList.size());
			ElementNode<?, T> newNode = this.elementBuilder.build();
			this.valueList.add(index, newNode);
			this.onValueChanged();
			return newNode;
		}
		
		public void onValueChanged()
		{
			this.changeListener.run();
		}
	}
	
	private static class ElementBuilderChooser<T> implements IConfigSpecVisitor<ListImpl<T>, IElementBuilder<T>>
	{

		@SuppressWarnings("unchecked")
		@Override
		public <S> IElementBuilder<T> visitProperty(IConfigPrimitiveSpec<S> propertySpec, ListImpl<T> list)
		{
			// Here S is the same as T
			return () -> new ElementPrimitive<>((IConfigPrimitiveSpec<T>)propertySpec, list);
		}

		@Override
		public IElementBuilder<T> visitTable(IConfigTableSpec tableSpec, ListImpl<T> list)
		{
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <U> IElementBuilder<T> visitList(IConfigListSpec<U> listSpec, ListImpl<T> list)
		{
			// Here T is a List<?> so it is ok to cast to List<?> and cast back to T
			return () -> (ElementNode<?, T>)new ElementList(listSpec, (ListImpl<List<?>>)list);
		}
		
	}
}

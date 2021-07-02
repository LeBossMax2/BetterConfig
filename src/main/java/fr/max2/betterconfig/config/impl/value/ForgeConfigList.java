package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

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

public class ForgeConfigList<T> extends ForgeConfigProperty<IConfigListSpec, List<T>> implements IConfigList
{
	private final ListImpl<T> list;

	public ForgeConfigList(IConfigListSpec spec, Consumer<ForgeConfigProperty<?, ?>> changeListener, ConfigValue<List<T>> configValue)
	{
		super(spec, changeListener, configValue);
		this.list = new ListImpl<>(ind -> this.onValueChanged(), spec, configValue.get());
	}

	@Override
	protected List<T> getCurrentValue()
	{
		return this.list.getCurrentValue();
	}

	@Override
	public List<? extends IConfigNode<?>> getValueList()
	{
		return this.list.getValueList();
	}

	@Override
	public IConfigNode<?> addValue()
	{
		return this.list.addValue();
	}
	
	public static interface IElementBuilder<T>
	{
		ElementNode<?, T> build(int index, T initialValue);
	}
	
	public static abstract class ElementNode<Spec extends IConfigSpecNode, T> implements IConfigNode<Spec>
	{
		private final Spec spec;
		protected ListImpl<T> parent;
		protected int index;
		
		public ElementNode(Spec spec, ListImpl<T> parent, int index)
		{
			this.spec = spec;
			this.parent = parent;
			this.index = index;
		}
		
		@Override
		public Spec getSpec()
		{
			return this.spec;
		}
		
		protected abstract T getCurrentValue();
	}
	
	public static class ElementPrimitive<T> extends ElementNode<IConfigPrimitiveSpec<T>, T> implements IConfigPrimitive<T>
	{
		private T currentValue;
		
		public ElementPrimitive(IConfigPrimitiveSpec<T> spec, ListImpl<T> parent, int index, T initialValue)
		{
			super(spec, parent, index);
			this.currentValue = initialValue;
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
		public void setValue(T value)
		{
			this.currentValue = value;
			this.parent.onValueChanged(this.index);
		}
	}
	
	public static class ElementList<T> extends ElementNode<IConfigListSpec, List<T>> implements IConfigList
	{
		private final ListImpl<T> list;
		
		public ElementList(IConfigListSpec spec, ListImpl<List<T>> parent, int index, List<T> initialValue)
		{
			super(spec, parent, index);
			this.list = new ListImpl<>(ind -> parent.onValueChanged(this.index), spec, initialValue);
		}

		@Override
		public List<? extends IConfigNode<?>> getValueList()
		{
			return this.list.getValueList();
		}

		@Override
		public IConfigNode<?> addValue()
		{
			return this.list.addValue();
		}

		@Override
		protected List<T> getCurrentValue()
		{
			return this.list.getCurrentValue();
		}
	}
	
	private static final class ListImpl<T>
	{
		private final IntConsumer changeListener;
		private final IElementBuilder<T> elementBuilder;
		private final List<ElementNode<?, T>> valueList;
		private final List<IConfigNode<?>> valueListView;
		private final List<T> currentValue;

		public ListImpl(IntConsumer changeListener, IConfigListSpec specs, List<T> initialValue)
		{
			this.changeListener = changeListener;
			this.elementBuilder = specs.getElementSpec().exploreNode(new ElementBuilderChooser<>(), this);

			this.valueList = new ArrayList<>();
			for (int i = 0; i < initialValue.size(); i++)
			{
				this.valueList.add(this.elementBuilder.build(i, initialValue.get(i)));
			}
			this.valueListView = Collections.unmodifiableList(this.valueList);
			this.currentValue = new MappedListView<>(this.valueList, elem -> elem.getCurrentValue());
		}

		protected List<T> getCurrentValue()
		{
			return this.currentValue;
		}

		public List<? extends IConfigNode<?>> getValueList()
		{
			return this.valueListView;
		}

		public IConfigNode<?> addValue()
		{
			int index = this.valueList.size();
			ElementNode<?, T> newNode = this.elementBuilder.build(index, null);
			this.onValueChanged(index);
			this.changeListener.accept(index);
			return newNode;
		}
		
		public void onValueChanged(int index)
		{
			this.changeListener.accept(index);
		}
	}
	
	private static class ElementBuilderChooser<T> implements IConfigSpecVisitor<ListImpl<T>, IElementBuilder<T>>
	{

		@SuppressWarnings("unchecked")
		@Override
		public <S> IElementBuilder<T> visitProperty(IConfigPrimitiveSpec<S> propertySpec, ListImpl<T> list)
		{
			// Here S is the same as T
			return (index, val) -> new ElementPrimitive<>((IConfigPrimitiveSpec<T>)propertySpec, list, index, val);
		}

		@Override
		public IElementBuilder<T> visitTable(IConfigTableSpec tableSpec, ListImpl<T> list)
		{
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public IElementBuilder<T> visitList(IConfigListSpec listSpec, ListImpl<T> list)
		{
			// Here T is a List<?> so it is ok to cast to List<?> and cast back to T
			return (index, val) -> (ElementNode<?, T>)new ElementList(listSpec, (ListImpl<List<?>>)list, index, (List<?>)val);
		}
		
	}
}

package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.config.impl.IForgeNodeInfo;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecVisitor;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.util.MappedListView;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ForgeConfigList<T, Info extends IForgeNodeInfo> extends ForgeConfigNode<List<T>, IConfigListSpec<T>, Info> implements IConfigList<T>
{
	/** The translation key for the label of elements of a list */
	public static final String LIST_ELEMENT_LABEL_KEY = BetterConfig.MODID + ".list.child";
	
	private final IElementBuilder<T> elementBuilder;
	private final List<ForgeConfigNode<T, ?, ListChildInfo>> valueList;
	private final List<IConfigNode<T>> valueListView;
	private final List<T> currentValue;

	public ForgeConfigList(IConfigListSpec<T> spec, Info info, List<T> initialValue)
	{
		super(spec, info);
		this.elementBuilder = spec.getElementSpec().exploreNode(new ElementBuilderChooser<>(), this);

		this.valueList = new ArrayList<>();
		this.valueListView = Collections.unmodifiableList(this.valueList);
		this.currentValue = new MappedListView<>(this.valueList, elem -> elem.getCurrentValue());
		if (initialValue != null)
		{
			for (int i = 0; i < initialValue.size(); i++)
			{
				T val = initialValue.get(i);
				ForgeConfigNode<T, ?, ListChildInfo> elem = this.elementBuilder.build(val);
				elem.info.setIndex(i);
				this.valueList.add(elem);
			}
		}
	}
	
	public ForgeConfigList<T, Info> addChangeListener(Runnable listener)
	{
		this.elemChangeListeners.add(listener);
		return this;
	}

	@Override
	protected List<T> getCurrentValue()
	{
		return this.currentValue;
	}

	@Override
	public List<? extends IConfigNode<T>> getValueList()
	{
		return this.valueListView;
	}

	@Override
	public void removeValueAt(int index)
	{
		this.valueList.remove(index);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
	}

	@Override
	public IConfigNode<T> addValue(int index)
	{
		Preconditions.checkPositionIndex(index, this.valueList.size());
		ForgeConfigNode<T, ?, ListChildInfo> newNode = this.elementBuilder.build(this.getSpec().getElementSpec().getDefaultValue());
		this.valueList.add(index, newNode);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
		return newNode;
	}
	
	private void updateElementIndicesFrom(int index)
	{
		for (int i = index; i < this.valueList.size(); i++)
		{
			this.valueList.get(i).info.setIndex(i);
		}
	}
	
	private void onValueChanged()
	{
		this.elemChangeListeners.forEach(Runnable::run);
	}
	
	private static interface IElementBuilder<T>
	{
		ForgeConfigNode<T, ?, ListChildInfo> build(T initialValue);
	}
	
	private static class ListChildInfo implements IForgeNodeInfo
	{
		private final ForgeConfigList<?, ?> parent;
		private int index;

		private ListChildInfo(ForgeConfigList<?, ?> parent)
		{
			this.parent = parent;
			this.index = -1;
		}
		
		private void setIndex(int index)
		{
			this.index = index;
		}

		@Override
		public String getName()
		{
			return this.parent.getName() + "[" + this.index + "]";
		}

		@Override
		public ITextComponent getDisplayName()
		{
			return new TranslationTextComponent(LIST_ELEMENT_LABEL_KEY, this.parent.getName(), this.index);
		}
		
		@Override
		public Stream<String> getPath()
		{
			return Stream.concat(this.parent.info.getPath(), Stream.of(Integer.toString(this.index)));
		}

		@Override
		public String getCommentString()
		{
			return this.parent.getCommentString();
		}

		@Override
		public List<? extends ITextComponent> getDisplayComment()
		{
			return this.parent.getDisplayComment();
		}
	}
	
	private static class ElementBuilderChooser<T> implements IConfigSpecVisitor<ForgeConfigList<T, ?>, IElementBuilder<T>>
	{
		@Override
		public IElementBuilder<T> visitTable(IConfigTableSpec tableSpec, ForgeConfigList<T, ?> list)
		{
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public <U> IElementBuilder<T> visitList(IConfigListSpec<U> listSpec, ForgeConfigList<T, ?> parentList)
		{
			// Here T is a List<U> so it is ok to cast to List<?> and cast back to T
			return (val) -> (ForgeConfigNode<T, ?, ListChildInfo>)new ForgeConfigList<>(listSpec, new ListChildInfo(parentList), (List<U>)val).addChangeListener(parentList::onValueChanged);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <S> IElementBuilder<T> visitPrimitive(IConfigPrimitiveSpec<S> primitiveSpec, ForgeConfigList<T, ?> parentList)
		{
			// Here S is the same as T
			return (val) -> new ForgeConfigPrimitive<>((IConfigPrimitiveSpec<T>)primitiveSpec, new ListChildInfo(parentList), val).addChangeListener(parentList::onValueChanged);
		}
	}
}

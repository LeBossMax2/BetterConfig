package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import fr.max2.betterconfig.util.property.list.ReadableLists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ForgeConfigList<Info extends IForgeNodeInfo> extends ForgeConfigNode<IConfigListSpec, Info> implements IConfigList
{
	/** The translation key for the label of elements of a list */
	public static final String LIST_ELEMENT_LABEL_KEY = BetterConfig.MODID + ".list.child";
	
	private final List<Runnable> elemChangeListeners = new ArrayList<>();
	private final IElementBuilder elementBuilder;
	private final List<?> initialValue;
	private final IReadableList<ForgeConfigNode<?, ListChildInfo>> valueList;
	private final IReadableList<IConfigNode> valueListView;
	private final List<?> currentValue;

	public ForgeConfigList(IConfigListSpec spec, Info info, List<?> initialValue)
	{
		super(spec, info);
		this.elementBuilder = spec.getElementSpec().exploreNode(new ElementBuilderChooser(), this);
		this.initialValue = initialValue;

		this.valueList = new ObservableList<>();
		this.valueListView = ReadableLists.unmodifiableList(this.valueList);
		this.currentValue = new MappedListView<>(this.valueList, elem -> elem.getCurrentValue());
		if (initialValue != null)
		{
			for (int i = 0; i < initialValue.size(); i++)
			{
				Object val = initialValue.get(i);
				ForgeConfigNode<?, ListChildInfo> elem = this.elementBuilder.build(val);
				elem.info.setIndex(i);
				this.valueList.add(elem);
			}
		}
	}
	
	public ForgeConfigList<Info> addChangeListener(Runnable listener)
	{
		this.elemChangeListeners.add(listener);
		return this;
	}

	@Override
	protected List<?> getCurrentValue()
	{
		return this.currentValue;
	}

	@Override
	public IReadableList<IConfigNode> getValueList()
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
	public IConfigNode addValue(int index)
	{
		Preconditions.checkPositionIndex(index, this.valueList.size());
		ForgeConfigNode<?, ListChildInfo> newNode = this.elementBuilder.build(this.getSpec().getElementSpec().getDefaultValue());
		this.valueList.add(index, newNode);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
		return newNode;
	}
	
	@Override
	public void undoChanges()
	{
		for (int i = 0; i < this.initialValue.size(); i++)
		{
			this.valueListView.get(i).undoChanges();
		}
		
		for (int i = this.initialValue.size() - 1; i >= this.initialValue.size(); i--)
		{
			this.valueListView.remove(i);
		}
		this.onValueChanged();
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
	
	@Override
	public String toString()
	{
		return "[" + getValueList().stream().map(val -> val.toString()).collect(Collectors.joining(", ")) + "]";
	}
	
	private static interface IElementBuilder
	{
		ForgeConfigNode<?, ListChildInfo> build(Object initialValue);
	}
	
	private static class ListChildInfo implements IForgeNodeInfo
	{
		private final ForgeConfigList<?> parent;
		private int index;

		private ListChildInfo(ForgeConfigList<?> parent)
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
		public Component getDisplayName()
		{
			return new TranslatableComponent(LIST_ELEMENT_LABEL_KEY, this.parent.getName(), this.index);
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
		public List<? extends Component> getDisplayComment()
		{
			return this.parent.getDisplayComment();
		}
	}
	
	private static class ElementBuilderChooser implements IConfigSpecVisitor<ForgeConfigList<?>, IElementBuilder>
	{
		@Override
		public IElementBuilder visitTable(IConfigTableSpec tableSpec, ForgeConfigList<?> list)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public IElementBuilder visitList(IConfigListSpec listSpec, ForgeConfigList<?> parentList)
		{
			// Here T is a List<U> so it is ok to cast to List<?> and cast back to T
			return val -> (ForgeConfigNode<?, ListChildInfo>)new ForgeConfigList<>(listSpec, new ListChildInfo(parentList), (List<?>)val).addChangeListener(parentList::onValueChanged);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <S> IElementBuilder visitPrimitive(IConfigPrimitiveSpec<S> primitiveSpec, ForgeConfigList<?> parentList)
		{
			// Here S is the same as T
			return val ->
			{
				ForgeConfigPrimitive<?, ListChildInfo> node = new ForgeConfigPrimitive<>(primitiveSpec, new ListChildInfo(parentList), (S)val);
				node.onChanged(newVal -> parentList.onValueChanged());
				return node;
			};
		}
	}
}

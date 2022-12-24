package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import fr.max2.betterconfig.util.property.list.ReadableLists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ForgeConfigList extends ForgeConfigNode<IConfigListSpec> implements IConfigList
{
	/** The translation key for the label of elements of a list */
	public static final String LIST_ELEMENT_LABEL_KEY = BetterConfig.MODID + ".list.child";
	
	private final List<Runnable> elemChangeListeners = new ArrayList<>();
	private final IConfigName identifier;
	private final IElementBuilder elementBuilder;
	private final List<?> initialValue;
	private final IReadableList<IConfigList.Entry> valueList;
	private final IReadableList<IConfigList.Entry> valueListView;
	private final List<?> currentValue;

	public ForgeConfigList(IConfigName identifier, IConfigListSpec spec, List<?> initialValue)
	{
		super(spec);
		this.identifier = identifier;
		this.elementBuilder = this.chooseElementBuilder(spec.getElementSpec());
		this.initialValue = initialValue;

		this.valueList = new ObservableList<>();
		this.valueListView = ReadableLists.unmodifiableList(this.valueList);
		this.currentValue = new MappedListView<>(this.valueList, entry -> ((ForgeConfigNode<?>)entry.node()).getCurrentValue());
		if (initialValue != null)
		{
			for (int i = 0; i < initialValue.size(); i++)
			{
				Object val = initialValue.get(i);
				var id = new ListChildInfo(this.identifier);
				ForgeConfigNode<?> elem = this.elementBuilder.build(id, val);
				id.setIndex(i);
				this.valueList.add(new Entry(id, elem));
			}
		}
	}
	
	public ForgeConfigList addChangeListener(Runnable listener)
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
	public IReadableList<IConfigList.Entry> getValueList()
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
	public IConfigList.Entry addValue(int index)
	{
		Preconditions.checkPositionIndex(index, this.valueList.size());
		var id = new ListChildInfo(this.identifier);
		ForgeConfigNode<?> newNode = this.elementBuilder.build(id, this.getSpec().getElementSpec().getDefaultValue());
		IConfigList.Entry entry = new IConfigList.Entry(id, newNode);
		this.valueList.add(index, entry);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
		return entry;
	}
	
	@Override
	public void undoChanges()
	{
		for (int i = 0; i < this.initialValue.size(); i++)
		{
			this.valueListView.get(i).node().undoChanges();
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
			((ListChildInfo)this.valueList.get(i).key()).setIndex(i);
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
	
	private IElementBuilder chooseElementBuilder(IConfigSpecNode specNode)
	{
		if (specNode instanceof IConfigTableSpec tableSpec)
		{
			throw new UnsupportedOperationException();
		}
		else if (specNode instanceof IConfigListSpec listSpec)
		{
			return (id, val) -> new ForgeConfigList(id, listSpec, (List<?>)val).addChangeListener(this::onValueChanged);
		}
		else if (specNode instanceof IConfigPrimitiveSpec<?> primitiveSpec)
		{
			return makePrimitiveElementBuilder(primitiveSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private <T> IElementBuilder makePrimitiveElementBuilder(IConfigPrimitiveSpec<T> primitiveSpec)
	{
		return (id, val) ->
		{
			@SuppressWarnings("unchecked")
			ForgeConfigPrimitive<?> node = new ForgeConfigPrimitive<>(primitiveSpec, (T)val);
			node.onChanged(newVal -> this.onValueChanged());
			return node;
		};
	}
	
	private static interface IElementBuilder
	{
		ForgeConfigNode<?> build(IConfigName identifier, Object initialValue);
	}
	
	private static class ListChildInfo implements IConfigName
	{
		private final IConfigName parent;
		private int index;

		private ListChildInfo(IConfigName parent)
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
		public List<String> getPath()
		{
			var res = new ArrayList<>(this.parent.getPath());
			res.add(Integer.toString(this.index));
			return res;
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
}

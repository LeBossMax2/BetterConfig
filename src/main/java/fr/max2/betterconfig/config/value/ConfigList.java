package fr.max2.betterconfig.config.value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import fr.max2.betterconfig.util.property.list.ReadableLists;
import net.minecraft.network.chat.Component;

public final class ConfigList implements IConfigNode
{
	/** The translation key for the label of elements of a list */
	public static final String LIST_ELEMENT_LABEL_KEY = BetterConfig.MODID + ".list.child";
	
	private final List<Runnable> elemChangeListeners = new ArrayList<>();
	private final IConfigListSpec spec;
	private final IConfigName identifier;
	private final Function<IConfigName, IConfigNode> elementBuilder;
	private final IReadableList<Entry> valueList;
	private final IReadableList<Entry> valueListView;
	private final List<?> currentValue;
	private int initialSize = 0;
	
	public ConfigList(IConfigListSpec spec, IConfigName identifier)
	{
		this.spec = spec;
		this.identifier = identifier;
		this.elementBuilder = this.chooseElementBuilder(spec.getElementSpec());
		this.valueList = new ObservableList<>();
		this.valueListView = ReadableLists.unmodifiableList(this.valueList);
		this.currentValue = new MappedListView<>(this.valueList, entry -> entry.node().getValue());
	}

	@Override
	public IConfigListSpec getSpec()
	{
		return spec;
	}
	
	public ConfigList addChangeListener(Runnable listener)
	{
		this.elemChangeListeners.add(listener);
		return this;
	}
	
	@Override
	public List<?> getValue()
	{
		return currentValue;
	}
	
	public IReadableList<Entry> getValueList()
	{
		return valueListView;
	}
	
	public void removeValueAt(int index)
	{
		this.valueList.remove(index);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
	}
	
	public Entry addValue(int index)
	{
		Preconditions.checkPositionIndex(index, this.valueList.size());
		var id = new ListChildInfo(this.identifier);
		var newNode = this.elementBuilder.apply(id);
		var entry = new Entry(id, newNode);
		this.valueList.add(index, entry);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
		return entry;
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
	public void setAsInitialValue()
	{
		this.initialSize = this.valueList.size();
	}
	
	@Override
	public void undoChanges()
	{
		for (int i = 0; i < this.initialSize; i++)
		{
			this.valueList.get(i).node().undoChanges();
		}
		
		for (int i = this.valueList.size() - 1; i >= this.initialSize; i--)
		{
			this.valueList.remove(i);
		}
		this.onValueChanged();
	}
	
	@Override
	public String toString()
	{
		return "[ " + getValueList().stream().map(val -> val.toString()).collect(Collectors.joining(", ")) + " ]";
	}
	
	private Function<IConfigName, IConfigNode> chooseElementBuilder(IConfigSpecNode specNode)
	{
		if (specNode instanceof IConfigTableSpec tableSpec)
		{
			throw new UnsupportedOperationException();
		}
		else if (specNode instanceof IConfigListSpec listSpec)
		{
			return id -> new ConfigList(listSpec, id).addChangeListener(this::onValueChanged);
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
	
	private <T> Function<IConfigName, IConfigNode> makePrimitiveElementBuilder(IConfigPrimitiveSpec<T> primitiveSpec)
	{
		return id ->
		{
			ConfigPrimitive<?> node = new ConfigPrimitive<>(primitiveSpec);
			node.onChanged(newVal -> this.onValueChanged());
			return node;
		};
	}

	public static record Entry
	(
		IConfigName key,
		IConfigNode node
	)
	{ }
}

class ListChildInfo implements IConfigName
{
	private final IConfigName parent;
	private int index;

	public ListChildInfo(IConfigName parent)
	{
		this.parent = parent;
		this.index = -1;
	}
	
	public void setIndex(int index)
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
		return Component.translatable(ConfigList.LIST_ELEMENT_LABEL_KEY, this.parent.getName(), this.index);
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

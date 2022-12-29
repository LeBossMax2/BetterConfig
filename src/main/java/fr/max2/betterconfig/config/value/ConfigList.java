package fr.max2.betterconfig.config.value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.config.ConfigName;
import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;
import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import fr.max2.betterconfig.util.property.list.ReadableLists;
import net.minecraft.network.chat.Component;

public final class ConfigList implements ConfigNode
{
	/** The translation key for the label of elements of a list */
	public static final String LIST_ELEMENT_LABEL_KEY = BetterConfig.MODID + ".list.child";

	private final List<Runnable> elemChangeListeners = new ArrayList<>();
	private final ConfigListSpec spec;
	private final ConfigName identifier;
	private final Function<ConfigName, ConfigNode> elementBuilder;
	private final IReadableList<Entry> valueList;
	private final IReadableList<Entry> valueListView;
	private final List<?> currentValue;
	private int initialSize = 0;

	private ConfigList(ConfigListSpec spec, ConfigName identifier)
	{
		this.spec = spec;
		this.identifier = identifier;
		this.elementBuilder = this.chooseElementBuilder(spec.elementSpec());
		this.valueList = new ObservableList<>();
		this.valueListView = ReadableLists.unmodifiableList(this.valueList);
		this.currentValue = new MappedListView<>(this.valueList, entry -> entry.node().getValue());
	}

	public static ConfigList make(ConfigName identifier, ConfigListSpec spec)
	{
		return new ConfigList(spec, identifier);
	}

	@Override
	public ConfigListSpec getSpec()
	{
		return this.spec;
	}

	public ConfigList addChangeListener(Runnable listener)
	{
		this.elemChangeListeners.add(listener);
		return this;
	}

	@Override
	public List<?> getValue()
	{
		return this.currentValue;
	}

	public IReadableList<Entry> getValueList()
	{
		return this.valueListView;
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
		for (var entry : this.valueList)
		{
			entry.node().setAsInitialValue();
		}
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
		return "[ " + this.getValueList().stream().map(val -> val.toString()).collect(Collectors.joining(", ")) + " ]";
	}

	private Function<ConfigName, ConfigNode> chooseElementBuilder(ConfigSpec specNode)
	{
		if (specNode instanceof ConfigTableSpec tableSpec)
		{
			throw new UnsupportedOperationException();
		}
		else if (specNode instanceof ConfigListSpec listSpec)
		{
			return id -> ConfigList.make(id, listSpec).addChangeListener(this::onValueChanged);
		}
		else if (specNode instanceof ConfigPrimitiveSpec<?> primitiveSpec)
		{
			return this.makePrimitiveElementBuilder(primitiveSpec);
		}
		else if (specNode instanceof ConfigUnknownSpec unknownSpec)
		{
			return id -> ConfigUnknown.make(id, unknownSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private <T> Function<ConfigName, ConfigNode> makePrimitiveElementBuilder(ConfigPrimitiveSpec<T> primitiveSpec)
	{
		return id ->
		{
			ConfigPrimitive<?> node = ConfigPrimitive.make(id, primitiveSpec);
			node.onChanged(newVal -> this.onValueChanged());
			return node;
		};
	}

	public static record Entry
	(
		ConfigName key,
		ConfigNode node
	)
	{ }
}

class ListChildInfo implements ConfigName
{
	private final ConfigName parent;
	private int index;

	public ListChildInfo(ConfigName parent)
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

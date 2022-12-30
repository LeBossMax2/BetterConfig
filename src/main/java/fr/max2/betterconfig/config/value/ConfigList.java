package fr.max2.betterconfig.config.value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;
import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import fr.max2.betterconfig.util.property.list.ReadableLists;

public final class ConfigList implements ConfigNode
{
	private final List<Runnable> elemChangeListeners = new ArrayList<>();
	private final ConfigListSpec spec;
	private final Supplier<ConfigNode> elementBuilder;
	private final IReadableList<Entry> valueList;
	private final IReadableList<Entry> valueListView;
	private final List<?> currentValue;
	private int initialSize = 0;

	private ConfigList(ConfigListSpec spec)
	{
		this.spec = spec;
		this.elementBuilder = this.chooseElementBuilder(spec.elementSpec());
		this.valueList = new ObservableList<>();
		this.valueListView = ReadableLists.unmodifiableList(this.valueList);
		this.currentValue = new MappedListView<>(this.valueList, entry -> entry.node().getValue());
	}

	public static ConfigList make(ConfigListSpec spec)
	{
		return new ConfigList(spec);
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
		var id = new Index();
		var newNode = this.elementBuilder.get();
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
			this.valueList.get(i).index().set(i);
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

	private Supplier<ConfigNode> chooseElementBuilder(ConfigSpec specNode)
	{
		if (specNode instanceof ConfigTableSpec tableSpec)
		{
			throw new UnsupportedOperationException();
		}
		else if (specNode instanceof ConfigListSpec listSpec)
		{
			return () -> ConfigList.make(listSpec).addChangeListener(this::onValueChanged);
		}
		else if (specNode instanceof ConfigPrimitiveSpec<?> primitiveSpec)
		{
			return this.makePrimitiveElementBuilder(primitiveSpec);
		}
		else if (specNode instanceof ConfigUnknownSpec unknownSpec)
		{
			return () -> ConfigUnknown.make(unknownSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private <T> Supplier<ConfigNode> makePrimitiveElementBuilder(ConfigPrimitiveSpec<T> primitiveSpec)
	{
		return () ->
		{
			ConfigPrimitive<?> node = ConfigPrimitive.make(primitiveSpec);
			node.onChanged(newVal -> this.onValueChanged());
			return node;
		};
	}

	public static record Entry
	(
		Index index,
		ConfigNode node
	)
	{ }

	public static class Index
	{
		private int index;

		public Index()
		{
			this.index = -1;
		}

		private void set(int index)
		{
			this.index = index;
		}

		public int get()
		{
			return this.index;
		}

		@Override
		public String toString()
		{
			return Integer.toString(this.index);
		}
	}
}

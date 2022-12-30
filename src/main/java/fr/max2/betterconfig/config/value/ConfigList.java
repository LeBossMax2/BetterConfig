package fr.max2.betterconfig.config.value;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;
import fr.max2.betterconfig.util.EventDispatcher;
import fr.max2.betterconfig.util.IEvent;
import fr.max2.betterconfig.util.MappedListView;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import fr.max2.betterconfig.util.property.list.ReadableLists;

/**
 * A node containing a list in a configuration tree
 */
public final class ConfigList implements ConfigNode
{
	private final EventDispatcher<Runnable> onChanged = EventDispatcher.ordered();
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

	/**
	 * Builds a {@code ConfigList} for the given specification
	 * @param spec the specification of the node to create
	 * @return the newly created list node
	 */
	public static ConfigList make(ConfigListSpec spec)
	{
		return new ConfigList(spec);
	}

	@Override
	public ConfigListSpec getSpec()
	{
		return this.spec;
	}

	/**
	 * Returns the event triggered when the value of this node changed
	 */
	public IEvent<Runnable> onChanged()
	{
		return this.onChanged;
	}

	@Override
	public List<?> getValue()
	{
		return this.currentValue;
	}

	/**
	 * Returns the list of entries in this list
	 */
	public IReadableList<Entry> getValueList()
	{
		return this.valueListView;
	}

	/**
	 * Adds a new entry to the list at the given index
	 * @param index the insertion index
	 * @return the newly created entry
	 */
	public Entry addValue(int index)
	{
		Preconditions.checkPositionIndex(index, this.valueList.size());
		var entry = new Entry(new Index(index), this.elementBuilder.get());
		this.valueList.add(index, entry);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
		return entry;
	}

	/**
	 * Removes the entry at the given index from the list
	 * @param index the removal index
	 */
	public void removeValueAt(int index)
	{
		this.valueList.remove(index);
		this.updateElementIndicesFrom(index);
		this.onValueChanged();
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
		this.onChanged.dispatch(Runnable::run);
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
			return () ->
			{
				var configList = ConfigList.make(listSpec);
				configList.onChanged().add(this::onValueChanged);
				return configList;
			};
		}
		else if (specNode instanceof ConfigPrimitiveSpec<?> primitiveSpec)
		{
			return () ->
			{
				var node = ConfigPrimitive.make(primitiveSpec);
				node.onChanged().add(newVal -> this.onValueChanged());
				return node;
			};
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

	public static record Entry
	(
		/** The index holder of the entry */
		Index index,
		/** The configuration value of the entry */
		ConfigNode node
	)
	{ }

	/**
	 * Holds the index of an entry
	 * The value of the index may change when inserting or removing elements from the list
	 */
	public static class Index
	{
		private int index;

		private Index(int initialIndex)
		{
			this.index = initialIndex;
		}

		private void set(int index)
		{
			this.index = index;
		}

		/**
		 * Returns the current index
		 */
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

package fr.max2.betterconfig.config.impl.value;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.impl.ConfigLocation;
import fr.max2.betterconfig.config.impl.spec.ForgeSpec;
import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.config.value.ConfigUnknown;
import fr.max2.betterconfig.config.value.ConfigNode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfig
{
	public static ConfigTable make(ForgeConfigSpec forgeSpec, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		var table = ConfigTable.make(ForgeSpec.makeSpec(forgeSpec, getSpecComments(forgeSpec)));
		initializeTableNode(table, changeListener, forgeSpec.getValues());
		table.setAsInitialValue();
		return table;
	}

	/** Gets the comments from the spec */
	private static Function<ConfigLocation, String> getSpecComments(ForgeConfigSpec spec)
	{
		return loc -> spec.getLevelComment(loc.getPath());
	}

	private static void initializeTableNode(ConfigTable table, Consumer<ForgeConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		for (ConfigTable.Entry entry : table.entries())
		{
			initializeTableEntry(entry.node(), configValues.get(entry.key().getName()), changeListener);
		}
	}

	private static void initializeTableEntry(ConfigNode node, Object param, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		if (node instanceof ConfigTable tableNode)
		{
			initializeTableNode(tableNode, changeListener, (UnmodifiableConfig)param);
		}
		else if (node instanceof ConfigList listNode)
		{
			@SuppressWarnings("unchecked")
			var configVal = (ConfigValue<List<?>>)param;
			initializeListNode(listNode, configVal.get());
			ForgeConfigProperty<List<?>> property = new ForgeConfigProperty<>(configVal, listNode::getValue);
			listNode.onChanged().add(() -> changeListener.accept(property));
		}
		else if (node instanceof ConfigPrimitive<?> primitiveNode)
		{
			initializePrimitiveEntry(primitiveNode, param, changeListener);
		}
		else if (node instanceof ConfigUnknown unknownNode)
		{
			ConfigValue<?> configVal = (ConfigValue<?>)param;
			unknownNode.setValue(configVal.get());
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private static <T> void initializePrimitiveEntry(ConfigPrimitive<T> node, Object param, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		@SuppressWarnings("unchecked")
		ConfigValue<T> configVal = (ConfigValue<T>)param;
		node.setValue(configVal.get());
		ForgeConfigProperty<T> property = new ForgeConfigProperty<>(configVal, node::getValue);
		node.onChanged().add(newVal -> changeListener.accept(property));
	}

	private static void initializeListNode(ConfigList node, List<?> initialValue)
	{
		if (initialValue == null)
			return;

		for (int i = 0; i < initialValue.size(); i++)
		{
			Object val = initialValue.get(i);
			var entry = node.addValue(i);
			initializeNode(entry.node(), val);
		}
	}

	private static void initializeNode(ConfigNode node, Object value)
	{
		if (node instanceof ConfigTable tableNode)
		{
			throw new UnsupportedOperationException();
		}
		else if (node instanceof ConfigList listNode)
		{
			initializeListNode(listNode, (List<?>)value);
		}
		else if (node instanceof ConfigPrimitive<?> primitiveNode)
		{
			initializePrimitiveNode(primitiveNode, value);
		}
		else if (node instanceof ConfigUnknown unknownNode)
		{
			unknownNode.setValue(value);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private static <T> void initializePrimitiveNode(ConfigPrimitive<T> primitiveNode, Object value)
	{
		primitiveNode.setValue(primitiveNode.getSpec().valueClass().cast(value));
	}
}

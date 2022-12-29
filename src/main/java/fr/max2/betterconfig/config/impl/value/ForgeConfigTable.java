package fr.max2.betterconfig.config.impl.value;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.ConfigLocation;
import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.impl.spec.ForgeConfigTableSpec;
import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.config.value.ConfigUnknown;
import fr.max2.betterconfig.config.value.IConfigNode;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigTable
{
	private static void newForgeConfigTable(ConfigTable table, Consumer<ForgeConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		for (ConfigTable.Entry entry : table.getEntryValues())
		{
			childNode(entry.key(), entry.node(), changeListener, configValues);
		}
	}

	public static ConfigTable create(ForgeConfigSpec spec, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		ConfigTable table = ConfigTable.make(RootInfo.INSTANCE, ForgeConfigTableSpec.newForgeConfigTableSpec(spec, getSpecComments(spec)));
		newForgeConfigTable(table, changeListener, spec.getValues());
		table.setAsInitialValue();
		return table;
	}

	private static void childNode(IConfigName identifier, IConfigNode node, Consumer<ForgeConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		var param = configValues.get(identifier.getName());

		if (node instanceof ConfigTable tableNode)
		{
			newForgeConfigTable(tableNode, changeListener, (UnmodifiableConfig)param);
		}
		else if (node instanceof ConfigList listNode)
		{
			@SuppressWarnings("unchecked")
			var configVal = (ConfigValue<List<?>>)param;
			ForgeConfigList.newForgeConfigList(listNode, configVal.get());
			ForgeConfigProperty<List<?>> property = new ForgeConfigProperty<>(configVal, changeListener, listNode::getValue);
			listNode.addChangeListener(property::onValueChanged);
		}
		else if (node instanceof ConfigPrimitive<?> primitiveNode)
		{
			childPrimitiveNode(primitiveNode, param, changeListener);
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

	private static <T> void childPrimitiveNode(ConfigPrimitive<T> node, Object param, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		@SuppressWarnings("unchecked")
		ConfigValue<T> configVal = (ConfigValue<T>)param;
		node.setValue(configVal.get());
		ForgeConfigProperty<T> property = new ForgeConfigProperty<>(configVal, changeListener, node::getValue);
		node.onChanged(newVal -> property.onValueChanged());
	}

	/** Gets the comments from the spec */
	private static Function<ConfigLocation, String> getSpecComments(ForgeConfigSpec spec)
	{
		return loc -> spec.getLevelComment(loc.getPath());
	}

	private static enum RootInfo implements IConfigName
	{
		INSTANCE;

		@Override
		public String getName()
		{
			return "";
		}

		@Override
		public Component getDisplayName()
		{
			return Component.empty();
		}

		@Override
		public List<String> getPath()
		{
			return List.of();
		}

		@Override
		public String getCommentString()
		{
			return "";
		}

		@Override
		public List<? extends Component> getDisplayComment()
		{
			return List.of(Component.literal(""));
		}
	}
}

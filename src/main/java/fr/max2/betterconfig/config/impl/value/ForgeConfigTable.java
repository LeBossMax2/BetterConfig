package fr.max2.betterconfig.config.impl.value;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;

import fr.max2.betterconfig.config.impl.IForgeNodeInfo;
import fr.max2.betterconfig.config.impl.spec.ForgeConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigLocation;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigTable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigTable<Info extends IForgeNodeInfo> extends ForgeConfigNode<IConfigTableSpec, Info> implements IConfigTable
{
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;

	private final List<IConfigNode> entryValues;
	/** The function to call when the value is changed */
	protected final Consumer<ForgeConfigProperty<?>> changeListener;

	private ForgeConfigTable(IConfigTableSpec spec, Info info, Consumer<ForgeConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		super(spec, info);
		this.changeListener = changeListener;
		
		this.configValues = configValues;
		ImmutableList.Builder<IConfigNode> builder = ImmutableList.builder();
		for (ConfigTableEntrySpec entry : this.getSpec().getEntrySpecs())
		{
			builder.add(childNode(entry.getLoc().getName(), entry));
		}
		this.entryValues = builder.build();
	}
	
	public static ForgeConfigTable<?> create(ForgeConfigSpec spec, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		return new ForgeConfigTable<>(new ForgeConfigTableSpec(spec, getSpecComments(spec)), RootInfo.INSTANCE, changeListener, spec.getValues());
	}

	@Override
	protected UnmodifiableConfig getCurrentValue()
	{
		return this.configValues;
	}
	
	@Override
	public List<IConfigNode> getEntryValues()
	{
		return this.entryValues;
	}
	
	@Override
	public void undoChanges()
	{
		this.entryValues.forEach(IConfigNode::undoChanges);
	}
	
	private IConfigNode childNode(String key, ConfigTableEntrySpec spec)
	{
		var info = new TableChildInfo(this, spec);
		var specNode = spec.getNode();
		var param = this.configValues.get(key);
		
		if (specNode instanceof IConfigTableSpec tableSpec)
		{
			return new ForgeConfigTable<>(tableSpec, info, this.changeListener, (UnmodifiableConfig)param);
		}
		else if (specNode instanceof IConfigListSpec listSpec)
		{
			@SuppressWarnings("unchecked")
			var configVal = (ConfigValue<List<?>>)param;
			ForgeConfigList<TableChildInfo> node = new ForgeConfigList<>(listSpec, info, configVal.get());
			ForgeConfigProperty<List<?>> property = new ForgeConfigProperty<>(configVal, this.changeListener, node::getCurrentValue);
			node.addChangeListener(property::onValueChanged);
			return node;
		}
		else if (specNode instanceof IConfigPrimitiveSpec<?> primitiveSpec)
		{
			return this.childPrimitiveNode(primitiveSpec, info, param);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private <T> IConfigNode childPrimitiveNode(IConfigPrimitiveSpec<T> primitiveSpec, TableChildInfo info, Object param)
	{
		@SuppressWarnings("unchecked")
		ConfigValue<T> configVal = (ConfigValue<T>)param;
		ForgeConfigPrimitive<T, TableChildInfo> node = new ForgeConfigPrimitive<>(primitiveSpec, info, configVal.get());
		ForgeConfigProperty<T> property = new ForgeConfigProperty<>(configVal, this.changeListener, node::getValue);
		node.onChanged(newVal -> property.onValueChanged());
		return node;
	}
	
	/** Gets the comments from the spec */
	private static Function<ConfigLocation, String> getSpecComments(ForgeConfigSpec spec)
	{
		return loc -> spec.getLevelComment(loc.getPath());
	}
	
	private static enum RootInfo implements IForgeNodeInfo
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
			return new TextComponent("");
		}
		
		@Override
		public Stream<String> getPath()
		{
			return Stream.empty();
		}

		@Override
		public String getCommentString()
		{
			return "";
		}

		@Override
		public List<? extends Component> getDisplayComment()
		{
			return Arrays.asList(new TextComponent(""));
		}
	}
	
	private static class TableChildInfo implements IForgeNodeInfo
	{
		private final ForgeConfigTable<?> parent;
		private final ConfigTableEntrySpec entry;

		private TableChildInfo(ForgeConfigTable<?> parent, ConfigTableEntrySpec entry)
		{
			this.parent = parent;
			this.entry = entry;
		}

		@Override
		public String getName()
		{
			return this.entry.getLoc().getName();
		}

		@Override
		public Component getDisplayName()
		{
			return this.entry.getDisplayName();
		}
		
		@Override
		public Stream<String> getPath()
		{
			return Stream.concat(this.parent.info.getPath(), Stream.of(this.entry.getLoc().getName()));
		}

		@Override
		public String getCommentString()
		{
			return this.entry.getCommentString();
		}

		@Override
		public List<? extends Component> getDisplayComment()
		{
			return this.entry.getDisplayComment();
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("{");
		
		List<ConfigTableEntrySpec> specs = this.getSpec().getEntrySpecs();
		
		boolean fist = true;
		for (int i = 0; i < specs.size(); i++)
		{
			if (fist)
			{
				builder.append(", ");
				fist = false;
			}
			builder.append(specs.get(i).getLoc().getName());
			builder.append(": ");
			builder.append(this.entryValues.get(i).toString());
		}	
		
		builder.append('}');
		return builder.toString();
	}
}

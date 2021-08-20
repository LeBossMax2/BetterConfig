package fr.max2.betterconfig.config.impl.value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.impl.IForgeNodeInfo;
import fr.max2.betterconfig.config.impl.spec.ForgeConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecVisitor;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.util.MappedMapView;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigTable<Info extends IForgeNodeInfo> extends ForgeConfigNode<UnmodifiableConfig, IConfigTableSpec, Info> implements IConfigTable
{
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;

	private final Map<String, IConfigNode<?>> valueMap;
	/** The function to call when the value is changed */
	protected final Consumer<ForgeConfigProperty<?>> changeListener;

	private ForgeConfigTable(IConfigTableSpec spec, Info info, Consumer<ForgeConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		super(spec, info);
		this.changeListener = changeListener;
		
		this.configValues = configValues;
		this.valueMap = new MappedMapView<>(this.getSpec().getSpecMap(), (key, value) -> childNode(key, value));
	}
	
	public static ForgeConfigTable<?> create(ForgeConfigSpec spec, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		return new ForgeConfigTable<>(new ForgeConfigTableSpec(spec), RootInfo.INSTANCE, changeListener, spec.getValues());
	}

	@Override
	protected UnmodifiableConfig getCurrentValue()
	{
		return this.configValues;
	}
	
	@Override
	public Map<String, ? extends IConfigNode<?>> getValueMap()
	{
		return this.valueMap;
	}
	
	@Override
	public void undoChanges()
	{
		this.valueMap.values().forEach(IConfigNode::undoChanges);
	}
	
	private IConfigNode<?> childNode(String key, ConfigTableEntrySpec spec)
	{
		return spec.getNode().exploreNode(new ConfigNodeCreator(this.changeListener, new TableChildInfo(this, spec)), this.configValues.get(key));
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
		public ITextComponent getDisplayName()
		{
			return new StringTextComponent("");
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
		public List<? extends ITextComponent> getDisplayComment()
		{
			return Arrays.asList(new StringTextComponent(""));
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
		public ITextComponent getDisplayName()
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
		public List<? extends ITextComponent> getDisplayComment()
		{
			return this.entry.getDisplayComment();
		}
	}
	
	private static class ConfigNodeCreator implements IConfigSpecVisitor<Object, IConfigNode<?>>
	{
		private final Consumer<ForgeConfigProperty<?>> changeListener;
		private final TableChildInfo info;

		public ConfigNodeCreator(Consumer<ForgeConfigProperty<?>> changeListener, TableChildInfo info)
		{
			this.changeListener = changeListener;
			this.info = info;
		}

		@Override
		public IConfigNode<?> visitTable(IConfigTableSpec tableSpec, Object param)
		{
			return new ForgeConfigTable<>(tableSpec, this.info, this.changeListener, (UnmodifiableConfig)param);
		}

		@Override
		public <T> IConfigNode<?> visitList(IConfigListSpec<T> listSpec, Object param)
		{
			return buildList(listSpec, param);
		}

		@SuppressWarnings("unchecked")
		private <T> IConfigNode<?> buildList(IConfigListSpec<T> listSpec, Object param)
		{
			ConfigValue<List<T>> configVal = (ConfigValue<List<T>>)param;
			ForgeConfigList<T, TableChildInfo> node = new ForgeConfigList<>(listSpec, this.info, configVal.get());
			ForgeConfigProperty<List<T>> property = new ForgeConfigProperty<>(configVal, this.changeListener, node::getCurrentValue);
			node.addChangeListener(property::onValueChanged);
			return node;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> IConfigNode<?> visitPrimitive(IConfigPrimitiveSpec<T> primitiveSpec, Object param)
		{
			ConfigValue<T> configVal = (ConfigValue<T>)param;
			ForgeConfigPrimitive<T, TableChildInfo> node = new ForgeConfigPrimitive<>(primitiveSpec, this.info, configVal.get());
			ForgeConfigProperty<T> property = new ForgeConfigProperty<>(configVal, this.changeListener, node::getValue);
			node.addChangeListener(property::onValueChanged);
			return node;
		}
	}
}

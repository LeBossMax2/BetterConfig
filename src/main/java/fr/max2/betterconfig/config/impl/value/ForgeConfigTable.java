package fr.max2.betterconfig.config.impl.value;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.impl.spec.ForgeConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecVisitor;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.ConfigTableEntry;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.util.MappedMapView;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigTable extends ForgeConfigNode<IConfigTableSpec> implements IConfigTable
{
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;

	private Map<String, ConfigTableEntry> valueMap;

	private ForgeConfigTable(IConfigTableSpec spec, Consumer<ForgeConfigProperty<?, ?>> changeListener, UnmodifiableConfig configValues)
	{
		super(spec, changeListener);
		
		this.configValues = configValues;
	}
	
	public ForgeConfigTable(ForgeConfigSpec spec, Consumer<ForgeConfigProperty<?, ?>> changeListener)
	{
		this(new ForgeConfigTableSpec(spec), changeListener, spec.getValues());
	}
	
	public Map<String, ConfigTableEntry> getValueMap()
	{
		if (this.valueMap == null)
		{
			this.valueMap = new MappedMapView<>(this.getSpec().getSpecMap(), (key, value) -> childNode(key, value));
		}
		return this.valueMap;
	}
	
	private ConfigTableEntry childNode(String key, ConfigTableEntrySpec spec)
	{
		return spec.getNode().exploreNode(new ConfigNodeCreator(this.changeListener, spec), this.configValues.get(key));
	}
	
	private static class ConfigNodeCreator implements IConfigSpecVisitor<Object, ConfigTableEntry>
	{
		private final Consumer<ForgeConfigProperty<?, ?>> changeListener;
		private final ConfigTableEntrySpec entrySpec;

		public ConfigNodeCreator(Consumer<ForgeConfigProperty<?, ?>> changeListener, ConfigTableEntrySpec entrySpec)
		{
			this.changeListener = changeListener;
			this.entrySpec = entrySpec;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> ConfigTableEntry visitProperty(IConfigPrimitiveSpec<T> propertySpec, Object param)
		{
			return new ConfigTableEntry(this.entrySpec, new ForgeConfigPrimitive<>(propertySpec, this.changeListener, (ConfigValue<T>)param));
		}

		@Override
		public ConfigTableEntry visitTable(IConfigTableSpec tableSpec, Object param)
		{
			return new ConfigTableEntry(this.entrySpec, new ForgeConfigTable(tableSpec, this.changeListener, (UnmodifiableConfig)param));
		}

		@Override
		public ConfigTableEntry visitList(IConfigListSpec listSpec, Object param)
		{
			return new ConfigTableEntry(this.entrySpec, buildList(listSpec, param));
		}

		@SuppressWarnings("unchecked")
		private <T> IConfigNode<?> buildList(IConfigListSpec listSpec, Object param)
		{
			return new ForgeConfigList<>(listSpec, this.changeListener, (ConfigValue<List<T>>)param);
		}
	}
}

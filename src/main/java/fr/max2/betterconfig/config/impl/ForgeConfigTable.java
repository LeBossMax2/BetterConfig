package fr.max2.betterconfig.config.impl;

import java.util.Map;
import java.util.function.Consumer;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.spec.ConfigValueSpec;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecVisitor;
import fr.max2.betterconfig.config.value.ConfigProperty;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.config.value.ConfigTableEntry;
import fr.max2.betterconfig.util.MappedMapView;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigTable extends ConfigTable
{
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;

	private Map<String, ConfigTableEntry> valueMap;

	private ForgeConfigTable(ConfigTableSpec spec, Consumer<ConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		super(spec, changeListener);
		
		this.configValues = configValues;
	}
	
	public ForgeConfigTable(ForgeConfigSpec spec, Consumer<ConfigProperty<?>> changeListener)
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
		private final Consumer<ConfigProperty<?>> changeListener;
		private final ConfigTableEntrySpec entrySpec;

		public ConfigNodeCreator(Consumer<ConfigProperty<?>> changeListener, ConfigTableEntrySpec entrySpec)
		{
			this.changeListener = changeListener;
			this.entrySpec = entrySpec;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> ConfigTableEntry visitProperty(ConfigValueSpec<T> propertySpec, Object param)
		{
			return new ConfigTableEntry(this.entrySpec, new ForgeConfigProperty<>(propertySpec, (ConfigValue<T>)param, this.changeListener));
		}

		@Override
		public ConfigTableEntry visitTable(ConfigTableSpec tableSpec, Object param)
		{
			return new ConfigTableEntry(this.entrySpec, new ForgeConfigTable(tableSpec, this.changeListener, (UnmodifiableConfig)param));
		}
	}
}

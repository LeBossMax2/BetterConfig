package fr.max2.betterconfig.config.value;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import fr.max2.betterconfig.config.spec.ConfigPropertySpec;
import fr.max2.betterconfig.config.spec.ConfigSpecNode;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecVisitor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigTable extends ConfigNode<ConfigTableSpec>
{
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;

	/** The function to call then the value of a property is changed */
	private Consumer<ConfigProperty<?>> changeListener;
	
	private Map<String, ConfigNode<?>> valueMap;

	private final ConfigNodeCreator nodeCreator = new ConfigNodeCreator();
	
	private ConfigTable(ConfigTableSpec spec, UnmodifiableConfig configValues, Consumer<ConfigProperty<?>> changeListener)
	{
		super(spec);
		
		this.configValues = configValues;
		this.changeListener = changeListener;
	}
	
	public ConfigTable(ForgeConfigSpec spec, Consumer<ConfigProperty<?>> changeListener)
	{
		this(new ConfigTableSpec(spec), spec.getValues(), changeListener);
	}
	
	public Map<String, ConfigNode<?>> getValueMap()
	{
		if (this.valueMap == null)
		{
			this.valueMap = this.getSpec().getSpecMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> childNode(entry.getKey(), entry.getValue())));
		}
		return this.valueMap;
	}
	
	private ConfigNode<?> childNode(String key, ConfigSpecNode spec)
	{
		return spec.exploreNode(this.nodeCreator, this.configValues.get(key));
	}
	
	public <R> Stream<R> exploreEntries(BiFunction<String, ConfigNode<?>, R> visitor)
	{
		return this.getValueMap().entrySet().stream().map(entry -> visitor.apply(entry.getKey(), entry.getValue()));
	}
	
	@Override
	public <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
	
	private class ConfigNodeCreator implements IConfigSpecVisitor<Object, ConfigNode<?>>
	{
		@SuppressWarnings("unchecked")
		@Override
		public <T> ConfigNode<?> visitProperty(ConfigPropertySpec<T> propertySpec, Object param)
		{
			return new ConfigProperty<>(propertySpec, (ConfigValue<T>)param, changeListener);
		}

		@Override
		public ConfigNode<?> visitTable(ConfigTableSpec tableSpec, Object param)
		{
			return new ConfigTable(tableSpec, (UnmodifiableConfig)param, changeListener);
		}
	}
}

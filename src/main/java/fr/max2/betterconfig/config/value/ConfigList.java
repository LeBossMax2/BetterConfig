package fr.max2.betterconfig.config.value;

import java.util.List;

import fr.max2.betterconfig.config.spec.ConfigListSpec;

public abstract class ConfigList<T>
{
	private final ConfigValue<List<T>> value;
	
	public ConfigList(ConfigListSpec<T> spec, List<T> initialValue)
	{
		super(spec, initialValue);
	}
	
	@Override
	public ConfigListSpec<T> getSpec()
	{
		return (ConfigListSpec<T>)super.getSpec();
	}
	
	public abstract List<ConfigNode<?>> getValueList();
	
	public abstract ConfigNode<?> addValue(T value);
}

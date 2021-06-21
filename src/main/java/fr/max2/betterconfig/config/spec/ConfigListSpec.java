package fr.max2.betterconfig.config.spec;

import java.util.List;
import java.util.stream.Collectors;

import fr.max2.betterconfig.config.ValueType;

public abstract class ConfigListSpec<T> extends ConfigValueSpec<List<T>>
{
	private final ConfigValueSpec<T> elementSpec;
	
	public ConfigListSpec(ConfigValueSpec<T> elementSpec)
	{
		super(List.class);
		this.elementSpec = elementSpec;
	}
	
	@Override
	public List<T> deepCopy(List<T> value)
	{
		return value.stream().map(this.elementSpec::deepCopy).collect(Collectors.toList());
	}

	public ValueType getElementType()
	{
		return this.elementSpec.getType();
	}
	
	public Class<?> getElementClass()
	{
		return this.elementSpec.getValueClass();
	}
	
	public ConfigValueSpec<T> getElementSpec()
	{
		return elementSpec;
	}
}

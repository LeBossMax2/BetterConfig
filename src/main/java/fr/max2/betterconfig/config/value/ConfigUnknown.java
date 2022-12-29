package fr.max2.betterconfig.config.value;

import java.util.Objects;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.ConfigSpec;

public final class ConfigUnknown implements IConfigNode
{
	private final ConfigSpec.Unknown spec;
	private Object initialValue;
	private Object value;

	private ConfigUnknown(ConfigSpec.Unknown spec)
	{
		this.spec = spec;
		this.initialValue = null;
		this.value = null;
	}

	public static ConfigUnknown make(IConfigName identifier, ConfigSpec.Unknown spec)
	{
		return new ConfigUnknown(spec);
	}

	@Override
	public ConfigSpec.Unknown getSpec()
	{
		return this.spec;
	}

	/**
	 * Gets the current configuration value
	 * @return the current value
	 */
	@Override
	public Object getValue()
	{
		return this.value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public void setAsInitialValue()
	{
		this.initialValue = this.value;
	}

	@Override
	public void undoChanges()
	{
		this.value = this.initialValue;
	}

	@Override
	public String toString()
	{
		return Objects.toString(this.getValue());
	}
}

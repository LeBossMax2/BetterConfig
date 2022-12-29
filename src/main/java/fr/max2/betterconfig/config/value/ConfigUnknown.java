package fr.max2.betterconfig.config.value;

import java.util.Objects;

import fr.max2.betterconfig.config.ConfigName;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;

public final class ConfigUnknown implements ConfigNode
{
	private final ConfigUnknownSpec spec;
	private Object initialValue;
	private Object value;

	private ConfigUnknown(ConfigUnknownSpec spec)
	{
		this.spec = spec;
		this.initialValue = null;
		this.value = null;
	}

	public static ConfigUnknown make(ConfigName identifier, ConfigUnknownSpec spec)
	{
		return new ConfigUnknown(spec);
	}

	@Override
	public ConfigUnknownSpec getSpec()
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

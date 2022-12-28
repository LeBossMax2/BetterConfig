package fr.max2.betterconfig.config.value;

import java.util.Objects;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.spec.ConfigSpecNode;

public final class ConfigUnknown implements IConfigNode
{
	private final ConfigSpecNode.Unknown spec;
	private Object initialValue;
	private Object value;

	public ConfigUnknown(ConfigSpecNode.Unknown spec)
	{
		this.spec = spec;
		this.initialValue = spec.node().getDefaultValue();
		this.value = this.initialValue;
	}

	@Override
	public ConfigSpecNode.Unknown getSpec()
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
		Preconditions.checkArgument(this.spec.node().getValueClass().isInstance(value));
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

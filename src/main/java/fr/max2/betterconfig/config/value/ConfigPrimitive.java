package fr.max2.betterconfig.config.value;

import java.util.HashSet;
import java.util.Set;

import fr.max2.betterconfig.config.spec.ConfigSpecNode;
import fr.max2.betterconfig.util.property.IListener;
import fr.max2.betterconfig.util.property.IReadableProperty;

public sealed class ConfigPrimitive<T> implements IConfigNode, IReadableProperty<T>
	permits
		ConfigPrimitive.Boolean,
		ConfigPrimitive.String,
		ConfigPrimitive.Enum,
		ConfigPrimitive.Number
{
	private final Set<IListener<? super T>> listeners = new HashSet<>();
	private final ConfigSpecNode.Primitive<T> spec;
	private T initialValue;
	private T currentValue;
	
	private ConfigPrimitive(ConfigSpecNode.Primitive<T> spec)
	{
		this.spec = spec;
		this.initialValue = spec.node().getDefaultValue();
		this.currentValue = this.initialValue;
	}

	public static ConfigPrimitive<?> make(ConfigSpecNode.Primitive<?> spec)
	{
		if (spec instanceof ConfigSpecNode.Boolean boolNode)
		{
			return new Boolean(boolNode);
		}
		else if (spec instanceof ConfigSpecNode.Number<?> numberNode)
		{
			return new Number<>(numberNode);
		}
		else if (spec instanceof ConfigSpecNode.String stringNode)
		{
			return new String(stringNode);
		}
		else if (spec instanceof ConfigSpecNode.Enum<?> enumNode)
		{
			return new Enum<>(enumNode);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public ConfigSpecNode.Primitive<T> getSpec()
	{
		return this.spec;
	}

	/**
	 * Gets the current configuration value
	 * @return the current temporary value
	 */
	@Override
	public T getValue()
	{
		return this.currentValue;
	}

	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	public void setValue(T newValue)
	{
		this.currentValue = newValue;
		this.onValiChanged();
	}
	
	@Override
	public void setAsInitialValue()
	{
		this.initialValue = this.currentValue;
	}
	
	@Override
	public void undoChanges()
	{
		this.setValue(this.initialValue);
	}

	@Override
	public void onChanged(IListener<? super T> listener)
	{
		this.listeners.add(listener);
	}
	
	@Override
	public void removeOnChangedListener(IListener<? super T> listener)
	{
		this.listeners.remove(listener);
	}
	
	private void onValiChanged()
	{
		this.listeners.forEach(l -> l.onValueChanged(this.currentValue));
	}
	
	@Override
	public java.lang.String toString()
	{
		return this.getValue().toString();
	}
	
	public static final class Boolean extends ConfigPrimitive<java.lang.Boolean>
	{
		private Boolean(ConfigSpecNode.Boolean spec)
		{
			super(spec);
		}
	}
	
	public static final class String extends ConfigPrimitive<java.lang.String>
	{
		private String(ConfigSpecNode.String spec)
		{
			super(spec);
		}
	}
	
	public static final class Enum<E extends java.lang.Enum<E>> extends ConfigPrimitive<E>
	{
		private Enum(ConfigSpecNode.Enum<E> spec)
		{
			super(spec);
		}
	}
	
	public static final class Number<N extends java.lang.Number> extends ConfigPrimitive<N>
	{
		private Number(ConfigSpecNode.Number<N> spec)
		{
			super(spec);
		}
	}
}

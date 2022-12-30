package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.PrimitiveType;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.util.EventDispatcher;
import fr.max2.betterconfig.util.IEvent;
import fr.max2.betterconfig.util.property.IListener;
import fr.max2.betterconfig.util.property.IReadableProperty;

/**
 * A node containing a primitive value in a configuration tree
 */
public sealed class ConfigPrimitive<T> implements ConfigNode, IReadableProperty<T>
	permits
		ConfigPrimitive.Boolean,
		ConfigPrimitive.String,
		ConfigPrimitive.Enum,
		ConfigPrimitive.Number
{
	private final EventDispatcher<IListener<? super T>> onChanged = EventDispatcher.unordered();
	private final ConfigPrimitiveSpec<T> spec;
	private T initialValue;
	private T currentValue;

	private ConfigPrimitive(ConfigPrimitiveSpec<T> spec)
	{
		this.spec = spec;
		this.initialValue = PrimitiveType.getType(spec.valueClass()).getDefaultValue();
		this.currentValue = this.initialValue;
	}

	/**
	 * Builds a {@code ConfigPrimitive} for the given specification
	 * @param spec the specification of the node to create
	 * @return the newly created primitive node
	 */
	public static ConfigPrimitive<?> make(ConfigPrimitiveSpec<?> spec)
	{
		if (spec instanceof ConfigPrimitiveSpec.Boolean boolNode)
		{
			return new Boolean(boolNode);
		}
		else if (spec instanceof ConfigPrimitiveSpec.Number<?> numberNode)
		{
			return new Number<>(numberNode);
		}
		else if (spec instanceof ConfigPrimitiveSpec.String stringNode)
		{
			return new String(stringNode);
		}
		else if (spec instanceof ConfigPrimitiveSpec.Enum<?> enumNode)
		{
			return new Enum<>(enumNode);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public ConfigPrimitiveSpec<T> getSpec()
	{
		return this.spec;
	}

	/**
	 * Gets the current configuration value
	 * @return the current value
	 */
	@Override
	public T getValue()
	{
		return this.currentValue;
	}

	/**
	 * Sets the configuration value
	 * @param newValue the new value
	 */
	public void setValue(T newValue)
	{
		this.currentValue = newValue;
		this.onValueChanged();
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
	public IEvent<IListener<? super T>> onChanged()
	{
		return this.onChanged;
	}

	private void onValueChanged()
	{
		this.onChanged.dispatch(l -> l.onValueChanged(this.currentValue));
	}

	@Override
	public java.lang.String toString()
	{
		return this.getValue().toString();
	}

	public static final class Boolean extends ConfigPrimitive<java.lang.Boolean>
	{
		private Boolean(ConfigPrimitiveSpec.Boolean spec)
		{
			super(spec);
		}
	}

	public static final class String extends ConfigPrimitive<java.lang.String>
	{
		private String(ConfigPrimitiveSpec.String spec)
		{
			super(spec);
		}
	}

	public static final class Enum<E extends java.lang.Enum<E>> extends ConfigPrimitive<E>
	{
		private Enum(ConfigPrimitiveSpec.Enum<E> spec)
		{
			super(spec);
		}
	}

	public static final class Number<N extends java.lang.Number> extends ConfigPrimitive<N>
	{
		private Number(ConfigPrimitiveSpec.Number<N> spec)
		{
			super(spec);
		}
	}
}

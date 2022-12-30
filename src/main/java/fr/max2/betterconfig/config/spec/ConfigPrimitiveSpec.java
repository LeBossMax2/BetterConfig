package fr.max2.betterconfig.config.spec;

/**
 * Represents the specification for a primitive value in a configuration
 */
public sealed abstract class ConfigPrimitiveSpec<T> implements ConfigSpec
	permits
		ConfigPrimitiveSpec.Boolean,
		ConfigPrimitiveSpec.String,
		ConfigPrimitiveSpec.Enum,
		ConfigPrimitiveSpec.Number
{
	/** The object holding the implementation-specific behavior */
	private final SpecData<T> data;

	public ConfigPrimitiveSpec(SpecData<T> data)
	{
		this.data = data;
	}

	/**
	 * Gets the class of the configuration value
	 */
	public abstract Class<T> valueClass();

	/**
	 * Gets the default configuration value
	 */
	public T getDefaultValue()
	{
		return this.data.getDefaultValue();
	}

	/**
	 * Checks if the given value is a valid value
	 * @param value the value to check
	 * @return true if the value matches the specification, false otherwise
	 */
	public boolean isAllowed(T value)
	{
		return this.data.isAllowed(value);
	}

	/**
	 * Corrects the given value to match the specification
	 * @param value the value to fix
	 * @return a valid value
	 */
	public T correct(T value)
	{
		return this.data.correct(value);
	}

	public static final class Boolean extends ConfigPrimitiveSpec<java.lang.Boolean>
	{
		public Boolean(SpecData<java.lang.Boolean> node)
		{
			super(node);
		}

		@Override
		public Class<java.lang.Boolean> valueClass()
		{
			return java.lang.Boolean.class;
		}
	}

	public static final class String extends ConfigPrimitiveSpec<java.lang.String>
	{

		public String(SpecData<java.lang.String> node)
		{
			super(node);
		}

		@Override
		public Class<java.lang.String> valueClass()
		{
			return java.lang.String.class;
		}
	}

	public static final class Enum<E extends java.lang.Enum<E>> extends ConfigPrimitiveSpec<E>
	{
		private final Class<E> valueClass;

		public Enum(Class<E> valueClass, SpecData<E> node)
		{
			super(node);
			this.valueClass = valueClass;
		}

		@Override
		public Class<E> valueClass()
		{
			return this.valueClass;
		}
	}

	public static final class Number<N extends java.lang.Number> extends ConfigPrimitiveSpec<N>
	{
		private final Class<N> valueClass;

		public Number(Class<N> valueClass, SpecData<N> node)
		{
			super(node);
			this.valueClass = valueClass;
		}

		@Override
		public Class<N> valueClass()
		{
			return this.valueClass;
		}
	}

	/** Defines the behavior of the {@link ConfigPrimitiveSpec} */
	public static interface SpecData<T>
	{
		/**
		 * Gets the default configuration value
		 */
		T getDefaultValue();

		/**
		 * Checks if the given value is a valid value
		 * @param value the value to check
		 * @return true if the value matches the specification, false otherwise
		 */
		boolean isAllowed(T value);

		/**
		 * Correct the given value to match the specification
		 * @param value the value to fix
		 * @return a valid value
		 */
		T correct(T value);
	}
}

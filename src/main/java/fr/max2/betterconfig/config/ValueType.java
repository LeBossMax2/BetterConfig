package fr.max2.betterconfig.config;

import java.util.function.Function;

import fr.max2.betterconfig.client.util.NumberTypes;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;

/**
 * Represents the type of values inside a config
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public enum ValueType
{
	BOOLEAN(Boolean.class, ConfigSpec.Boolean::new)
	{
		@Override
		public Object getDefaultValue(Class<?> valueClass)
		{
			return Boolean.FALSE;
		}
	},
	NUMBER(Number.class, ConfigSpec.Number::new)
	{
		@Override
		public Object getDefaultValue(Class<?> valueClass)
		{
			return NumberTypes.getType(valueClass).parse("0");
		}
	},
	STRING(String.class, ConfigSpec.String::new)
	{
		@Override
		public Object getDefaultValue(Class<?> valueClass)
		{
			return "";
		}
	},
	ENUM(Enum.class, ConfigSpec.Enum::new)
	{
		@Override
		public Object getDefaultValue(Class<?> valueClass)
		{
			return valueClass.getEnumConstants()[0];
		}
	};

	/** The super class corresponding to the type */
	private final Class<?> superClass;

	private final Function<IConfigPrimitiveSpec, ConfigSpec.Primitive> specConstructor;

	private ValueType(Class<?> clazz, Function<IConfigPrimitiveSpec, ConfigSpec.Primitive> specConstructor)
	{
		this.superClass = clazz;
		this.specConstructor = specConstructor;
	}

	/**
	 * Checks if the given class is of this type
	 * @param valueClass the class to check
	 * @return true if the class is of this type, false otherwise
	 */
	public boolean matches(Class<?> valueClass)
	{
		return this.superClass.isAssignableFrom(valueClass);
	}

	public abstract Object getDefaultValue(Class<?> valueClass);

	public <T> ConfigSpec.Primitive<T> makeSpec(IConfigPrimitiveSpec<T> innerSpec)
	{
		return this.specConstructor.apply(innerSpec);
	}
	/**
	 * Gets the {@code ValueType} corresponding to the given class
	 * @param valueClass
	 * @return the first type that matches the given class
	 */
	public static ValueType getType(Class<?> valueClass)
	{
		for (ValueType type : ValueType.values())
		{
			if (type.matches(valueClass))
			{
				return type;
			}
		}

		return null;
	}
}
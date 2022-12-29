package fr.max2.betterconfig.config;

import java.util.function.Function;

import fr.max2.betterconfig.client.util.NumberTypes;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec.SpecData;

/**
 * Represents the type of values inside a config
 */
public class PrimitiveType<T>
{
	private static final PrimitiveType<Boolean> BOOLEAN = new PrimitiveType<>(false, specData -> new ConfigPrimitiveSpec.Boolean(specData));
	private static final PrimitiveType<String> STRING = new PrimitiveType<>("", specData -> new ConfigPrimitiveSpec.String(specData));

	private static <T extends Number> PrimitiveType<T> newNumber(Class<T> valueClass)
	{
		return new PrimitiveType<>(NumberTypes.getType(valueClass).parse("0"), specData -> new ConfigPrimitiveSpec.Number<>(valueClass, specData));
	}

	private static <T extends Enum<T>> PrimitiveType<T> newEnum(Class<T> valueClass)
	{
		return new PrimitiveType<>(valueClass.getEnumConstants()[0], specData -> new ConfigPrimitiveSpec.Enum<>(valueClass, specData));
	}

	private final T defaultValue;
	private final Function<ConfigPrimitiveSpec.SpecData<T>, ConfigPrimitiveSpec<T>> specConstructor;

	private PrimitiveType(T defaultValue, Function<SpecData<T>, ConfigPrimitiveSpec<T>> specConstructor)
	{
		this.defaultValue = defaultValue;
		this.specConstructor = specConstructor;
	}

	public T getDefaultValue()
	{
		return this.defaultValue;
	}

	public ConfigPrimitiveSpec<T> makeSpec(ConfigPrimitiveSpec.SpecData<T> specData)
	{
		return this.specConstructor.apply(specData);
	}

	/**
	 * Gets the {@code PrimitiveType} corresponding to the given class
	 * @param valueClass
	 * @return the first type that matches the given class
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> PrimitiveType<T> getType(Class<T> valueClass)
	{
		if (Boolean.class == valueClass)
		{
			return (PrimitiveType)BOOLEAN;
		}
		else if (String.class == valueClass)
		{
			return (PrimitiveType)STRING;
		}
		else if (valueClass.isEnum())
		{
			return newEnum((Class)valueClass);
		}
		else if (Number.class.isAssignableFrom(valueClass))
		{
			return newNumber((Class)valueClass);
		}
		else
		{
			return null;
		}
	}
}
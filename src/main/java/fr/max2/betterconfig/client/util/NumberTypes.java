package fr.max2.betterconfig.client.util;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class NumberTypes
{
	/** The map of number type for each number class */
	private static final Map<Class<?>, INumberType<?>> TYPES = new IdentityHashMap<>();
	
	static
	{
		register(Byte.class, IntegerType.BYTE);
		register(Short.class, IntegerType.SHORT);
		register(Integer.class, IntegerType.INTERGER);
		register(Long.class, IntegerType.LONG);
		register(Float.class, RealType.FLOAT);
		register(Double.class, RealType.DOUBLE);
	}
	
	/** Registers a number type */
	public static <N> void register(Class<N> numberClass, INumberType<N> numberType)
	{
		Preconditions.checkNotNull(numberClass, "The number class shouldn't be null");
		Preconditions.checkNotNull(numberType, "The number type shouldn't be null");
		TYPES.put(numberClass, numberType);
	}
	
	/** Gets the number type associated with the given class */
	@SuppressWarnings("unchecked")
	public static <T> INumberType<T> getType(Class<T> numberClass)
	{
		Preconditions.checkNotNull(numberClass, "The number class shouldn't be null");
		return (INumberType<T>)TYPES.get(numberClass);
	}
}

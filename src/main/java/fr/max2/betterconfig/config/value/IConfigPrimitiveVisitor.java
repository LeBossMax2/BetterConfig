package fr.max2.betterconfig.config.value;

/**
 * An interface for visiting {@code IConfigPrimitive} according to the type of the value
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigPrimitiveVisitor<P, R>
{
	/**
	 * Visits a boolean property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitBoolean(ConfigPrimitive<Boolean> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a number property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitNumber(ConfigPrimitive<? extends Number> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a string property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitString(ConfigPrimitive<String> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits an enum property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default <E extends Enum<E>> R visitEnum(ConfigPrimitive<E> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a property of unknown type
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitUnknown(ConfigPrimitive<?> property, P param);
}

package fr.max2.betterconfig.config.spec;

/**
 * An interface for visiting {@code IConfigPrimitive} according to the type of the value
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigPrimitiveSpecVisitor<P, R>
{
	/**
	 * Visits a boolean property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitBoolean(IConfigPrimitiveSpec<Boolean> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a number property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitNumber(IConfigPrimitiveSpec<? extends Number> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a string property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitString(IConfigPrimitiveSpec<String> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits an enum property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default <E extends Enum<E>> R visitEnum(IConfigPrimitiveSpec<E> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a property of unknown type
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitUnknown(IConfigPrimitiveSpec<?> property, P param);
}

package fr.max2.betterconfig.config.value;

import java.util.List;

/**
 * An interface for visiting {@code ConfigProperty} according to the type of the value
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigPropertyVisitor<P, R>
{
	/**
	 * Visits a boolean property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitBoolean(ConfigValue<Boolean> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a number property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitNumber(ConfigValue<? extends Number> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a string property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitString(ConfigValue<String> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits an enum property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default <E extends Enum<E>> R visitEnum(ConfigValue<E> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a list property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	default R visitList(ConfigValue<? extends List<?>> property, P param)
	{
		return this.visitUnknown(property, param);
	}

	/**
	 * Visits a property of unknown type
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitUnknown(ConfigValue<?> property, P param);
}

package fr.max2.betterconfig.config;

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
	R visitBoolean(ConfigProperty<Boolean> property, P param);

	/**
	 * Visits a number property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitNumber(ConfigProperty<? extends Number> property, P param);

	/**
	 * Visits a string property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitString(ConfigProperty<String> property, P param);

	/**
	 * Visits an enum property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<E extends Enum<E>> R visitEnum(ConfigProperty<E> property, P param);

	/**
	 * Visits a list property
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitList(ConfigProperty<? extends List<?>> property, P param);

	/**
	 * Visits a property of unknown type
	 * @param property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitUnknown(ConfigProperty<?> property, P param);
}

package fr.max2.betterconfig.config.value;

import java.util.List;

public interface IConfigListVisitor<P, R>
{
	/**
	 * Visits a boolean list
	 * @param list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitBooleanList(ConfigProperty<List<Boolean>> list, P param);

	/**
	 * Visits a number list
	 * @param list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitNumberList(ConfigProperty<List<? extends Number>> list, P param);

	/**
	 * Visits a string list
	 * @param list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitStringList(ConfigProperty<List<String>> list, P param);

	/**
	 * Visits an enum list
	 * @param list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<E extends Enum<E>> R visitEnumList(ConfigProperty<List<E>> list, P param);

	/**
	 * Visits a list of lists
	 * @param list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitListList(ConfigProperty<List<? extends List<?>>> list, P param);

	/**
	 * Visits a list of unknown type
	 * @param list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitUnknownList(ConfigProperty<List<?>> list, P param);
}

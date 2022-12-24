package fr.max2.betterconfig.config.value;

/**
 * An interface for visiting {@code IConfigNode}s
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigValueVisitor<P, R>
{
	/**
	 * Visits a table node
	 * @param table the visited config table
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitTable(IConfigTable table, P param);

	/**
	 * Visits a list node
	 * @param list the visited config list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitList(IConfigList list, P param);

	/**
	 * Visits a property node
	 * @param <T> the type of the property
	 * @param primitive the visited config property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<T> R visitPrimitive(IConfigPrimitive<T> primitive, P param);
}

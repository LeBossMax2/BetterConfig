package fr.max2.betterconfig.config.value;

/**
 * An interface for visiting {@code ConfigNode}s
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigValueVisitor<P, R>
{
	/**
	 * Visits a property node
	 * @param <T> the type of the property
	 * @param property the visited config property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<T> R visitProperty(ConfigValue<T> property, P param);

	/**
	 * Visits a table node
	 * @param table the visited config table
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitTable(ConfigTable table, P param);
}

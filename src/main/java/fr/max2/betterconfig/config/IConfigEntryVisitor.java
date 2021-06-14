package fr.max2.betterconfig.config;

/**
 * An interface for visiting {@code ConfigTable} entries
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigEntryVisitor<P, R>
{
	/**
	 * Visits a property entry of the table
	 * @param <T> the type of the property
	 * @param key the name of the entry
	 * @param property the value of the entry
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<T> R visitValue(String key, ConfigProperty<T> property, P param);

	/**
	 * Visits a sub table
	 * @param key the name of the entry
	 * @param table the value of the entry
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitSubTable(String key, ConfigTable table, P param);
}

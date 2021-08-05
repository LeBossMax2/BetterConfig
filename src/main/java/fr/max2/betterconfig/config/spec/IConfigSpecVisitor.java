package fr.max2.betterconfig.config.spec;

/**
 * An interface for visiting {@code IConfigSpecNode}s
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigSpecVisitor<P, R>
{
	/**
	 * Visits a property spec node
	 * @param <T> the type of the property
	 * @param propertySpec the visited spec of the property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<T> R visitProperty(IConfigPrimitiveSpec<T> propertySpec, P param);

	/**
	 * Visits a table spec node
	 * @param tableSpec the visited spec of the table
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	R visitTable(IConfigTableSpec tableSpec, P param);

	/**
	 * Visits a list spec node
	 * @param listSpec the visited spec of the list
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<T> R visitList(IConfigListSpec<T> listSpec, P param);
}

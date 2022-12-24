package fr.max2.betterconfig.config.spec;

/**
 * An interface for visiting {@code IConfigSpecNode}s
 * @param <P> the type of parameter value
 * @param <R> the type of returned value
 */
public interface IConfigSpecVisitor<P, R>
{
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
	R visitList(IConfigListSpec listSpec, P param);
	
	/**
	 * Visits a primitive spec node
	 * @param <T> the type of the property
	 * @param primitiveSpec the visited spec of the property
	 * @param param the parameter of the visitor
	 * @return the resulting value of the visitor
	 */
	<T> R visitPrimitive(IConfigPrimitiveSpec<T> primitiveSpec, P param);
}

package fr.max2.betterconfig.config.spec;

public interface IConfigSpecNode<T>
{
	/**
	 * Gets the class of the configuration value
	 */
	Class<? super T> getValueClass();
	
	/**
	 * Gets the default configuration value
	 */
	T getDefaultValue();
	
	default <R> R exploreNode(IConfigSpecVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	<P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param);
}

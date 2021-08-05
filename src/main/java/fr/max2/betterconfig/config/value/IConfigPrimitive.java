package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;

public interface IConfigPrimitive<T> extends IConfigNode<T>
{
	/**
	 * Gets the current configuration value
	 * @return the current temporary value
	 */
	T getValue();
	
	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	void setValue(T value);
	
	@Override
	IConfigPrimitiveSpec<T> getSpec();
	
	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	default <R> R exploreType(IConfigPrimitiveVisitor<Void, R> visitor)
	{
		return this.exploreType(visitor, null);
	}

	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	default <P, R> R exploreType(IConfigPrimitiveVisitor<P, R> visitor, P param)
	{
		return this.getSpec().getType().exploreProperty(visitor, this, param);
	}
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitProperty(this, param);
	}
}

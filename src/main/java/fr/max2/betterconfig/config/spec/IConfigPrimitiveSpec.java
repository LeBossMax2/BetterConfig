package fr.max2.betterconfig.config.spec;

import fr.max2.betterconfig.config.ValueType;

public interface IConfigPrimitiveSpec<T> extends IConfigSpecNode
{
	/**
	 * Gets the class of the configuration value
	 */
	Class<? super T> getValueClass();
	
	default ValueType getType()
	{
		return ValueType.getType(this.getValueClass());
	}
	
	/**
	 * Checks if the given value is a valid value
	 * @param value the value to check
	 * @return true if the value matches the spec, false otherwise
	 */
	boolean isAllowed(T value);
	
	/**
	 * Correct the given value to match the spec
	 * @param value the value to fix
	 * @return a valid value
	 */
	T correct(T value);
	
	/**
	 * Gets the default configuration value
	 */
	T getDefaultValue();
	
	@Override
	default <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitProperty(this, param);
	}
}

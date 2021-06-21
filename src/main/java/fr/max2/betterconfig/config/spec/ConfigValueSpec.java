package fr.max2.betterconfig.config.spec;

import fr.max2.betterconfig.config.ValueType;

public abstract class ConfigValueSpec<T> extends ConfigSpecNode
{
	private final Class<?> valueClass;
	/** The type of the property */
	private final ValueType type;
	
	public ConfigValueSpec(Class<?> valueClass)
	{
		this.valueClass = valueClass;
		this.type = ValueType.getType(this.getValueClass());
	}
	
	/**
	 * Checks if the given value is a valid value
	 * @param value the value to check
	 * @return true if the value matches the spec, false otherwise
	 */
	public abstract boolean isAllowed(T value);
	
	/**
	 * Correct the given value to match the spec
	 * @param value the value to fix
	 * @return a valid value
	 */
	public abstract T correct(T value);

	public T deepCopy(T value)
	{
		return value;
	}
	
	/**
	 * Gets the default configuration value
	 */
	public abstract T getDefaultValue();
	
	public ValueType getType()
	{
		return this.type;
	}
	
	/**
	 * Gets the class of the configuration value
	 */
	public Class<?> getValueClass()
	{
		return this.valueClass;
	}
	
	@Override
	public <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitProperty(this, param);
	}
}

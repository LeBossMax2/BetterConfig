package fr.max2.betterconfig.config.spec;

public interface IConfigSpecNode
{
	/**
	 * Gets the class of the configuration value
	 */
	Class<?> getValueClass();

	/**
	 * Gets the default configuration value
	 */
	Object getDefaultValue();
}

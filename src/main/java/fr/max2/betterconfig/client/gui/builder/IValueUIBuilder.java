package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

import fr.max2.betterconfig.ConfigProperty;

/**
 * A builder for creating a user interface for a config property
 * @param <P> the type of user interface primitives
 */
public interface IValueUIBuilder<P>
{
	/**
	 * Builds a boolean value widget
	 * @param property the property to edit
	 * @return the primitive corresponding to the value user interface
	 */
	P buildBoolean(ConfigProperty<Boolean> property);
	
	/**
	 * Builds a number value widget
	 * @param property the property to edit
	 * @return the primitive corresponding to the value user interface
	 */
	P buildNumber(ConfigProperty<? extends Number> property);
	
	/**
	 * Builds a string value widget
	 * @param property the property to edit
	 * @return the primitive corresponding to the value user interface
	 */
	P buildString(ConfigProperty<String> property);
	
	/**
	 * Builds an enum value widget
	 * @param property the property to edit
	 * @return the primitive corresponding to the value user interface
	 */
	<E extends Enum<E>> P buildEnum(ConfigProperty<E> property);
	
	/**
	 * Builds a list value widget
	 * @param property the property to edit
	 * @return the primitive corresponding to the value user interface
	 */
	P buildList(ConfigProperty<? extends List<?>> property);
	
	/**
	 * Builds a value widget for an unknown type
	 * @param property the property to edit
	 * @return the primitive corresponding to the value user interface
	 */
	P buildUnknown(ConfigProperty<?> property);
}

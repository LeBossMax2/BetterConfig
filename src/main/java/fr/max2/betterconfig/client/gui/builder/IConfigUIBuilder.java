package fr.max2.betterconfig.client.gui.builder;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;

/**
 * A builder for creating config user interfaces
 * @param <P> the type of user interface primitives
 */
public interface IConfigUIBuilder<P>
{
	/**
	 * Starts the creation of a new config user interface
	 * @param screen the screen for which the user interface is created
	 * @return a builder to create a config table user interface
	 */
	ITableUIBuilder<P> start(BetterConfigScreen screen);
}

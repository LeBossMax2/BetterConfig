package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.config.ConfigFilter;

/** An interface for ui elements with a simple layout system */
public interface IBetterElement extends IGuiComponent
{
	/**
	 * Sets the y coordinate of this component to the given one and computes the height
	 * @param y the new y coordinate
	 * @return the computed height of this component
	 */
	int setYgetHeight(int y, ConfigFilter filter);
}
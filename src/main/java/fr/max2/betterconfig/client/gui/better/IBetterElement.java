package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.config.ConfigFilter;

/** An interface for ui elements with a simple layout system */
public interface IBetterElement extends IComponent
{
	boolean filterElements(ConfigFilter filter);
}
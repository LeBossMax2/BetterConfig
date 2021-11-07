package fr.max2.betterconfig.client.gui.style;

import fr.max2.betterconfig.client.gui.component.IComponent;

public interface IStylableComponent extends IComponent
{
	<T> T getProperty(PropertyIdentifier<T> property);
}

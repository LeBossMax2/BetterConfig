package fr.max2.betterconfig.client.gui.style;

public interface IPropertySource
{
	<T> T getProperty(PropertyIdentifier<T> property);
}

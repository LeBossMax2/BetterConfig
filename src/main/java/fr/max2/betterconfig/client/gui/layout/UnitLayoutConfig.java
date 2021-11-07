package fr.max2.betterconfig.client.gui.layout;

import fr.max2.betterconfig.client.gui.component.IComponent;

public enum UnitLayoutConfig implements ILayoutConfig<IComponent>
{
	INSTANCE;
	
	@Override
	public Size measureLayout(IComponent component)
	{
		return ComponentLayoutConfig.getParentRequiredSize(component, component.getStyleProperty(ComponentLayoutConfig.SIZE_OVERRIDE));
	}

	@Override
	public Rectangle computeLayout(Rectangle availableRect, IComponent component)
	{
		return ComponentLayoutConfig.getChildRect(component, availableRect);
	}
}

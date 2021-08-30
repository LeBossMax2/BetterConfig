package fr.max2.betterconfig.client.gui.layout;

import fr.max2.betterconfig.client.gui.component.IComponent;

public class UnitLayoutConfig extends LayoutConfig<IComponent>
{
	@Override
	public Size measureLayout(IComponent component)
	{
		return this.getParentRequiredSize(this.sizeOverride);
	}

	@Override
	public Rectangle computeLayout(Rectangle availableRect, IComponent layout)
	{
		return this.getChildRect(availableRect);
	}
}

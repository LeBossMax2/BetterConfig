package fr.max2.betterconfig.client.gui.layout;

import fr.max2.betterconfig.client.gui.component.IScrollComponent;

public enum ScrollPaneLayout implements ILayoutConfig<IScrollComponent>
{
	INSTANCE;
	
	@Override
	public Size measureLayout(IScrollComponent component)
	{
		Size size = new Size();
		size.width = component.getChild().measureLayout().width;
		size.combine(component.getStyleProperty(ComponentLayoutConfig.SIZE_OVERRIDE), null);
		return ComponentLayoutConfig.getParentRequiredSize(component, size);
	}

	@Override
	public Rectangle computeLayout(Rectangle availableRect, IScrollComponent component)
	{
		Rectangle rect = ComponentLayoutConfig.getChildRect(component, availableRect);
		Rectangle childRect = new Rectangle(0, 0, rect.size.width, rect.size.height);
		childRect.size.combine(component.getChild().getPrefSize(), null);
		component.getChild().computeLayout(childRect);
		component.setContentSize(childRect.size);
		return rect;
	}
}

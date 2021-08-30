package fr.max2.betterconfig.client.gui.component;

import fr.max2.betterconfig.client.gui.layout.LayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import net.minecraft.client.gui.GuiComponent;

public abstract class Component<LP> extends GuiComponent implements IComponent
{
	protected final IComponentParent layoutManager;
	protected Size prefSize;
	protected Rectangle relativeRect;
	protected Rectangle absoluteRect = new Rectangle();
	
	public Component(IComponentParent layoutManager)
	{
		this.layoutManager = layoutManager;
	}

	// Layout
	
	protected abstract LP getLayoutParam();

	protected abstract LayoutConfig<? super LP> getLayoutConfig();
	
	@Override
	public Size measureLayout()
	{
		Size prefSize = this.getLayoutConfig().measureLayout(this.getLayoutParam());
		this.setPrefSize(prefSize);
		return prefSize;
	}
	
	@Override
	public void computeLayout(Rectangle availableRect)
	{
		this.setRelativeRect(this.getLayoutConfig().computeLayout(availableRect, this.getLayoutParam()));
	}

	@Override
	public Size getPrefSize()
	{
		return this.prefSize;
	}
	
	protected Size getSize()
	{
		return this.relativeRect.size;
	}
	
	protected Rectangle getRect()
	{
		this.absoluteRect.x = this.layoutManager.getLayoutX() + this.relativeRect.x;
		this.absoluteRect.y = this.layoutManager.getLayoutY() + this.relativeRect.y;
		return this.absoluteRect;
	}

	protected void setPrefSize(Size prefSize)
	{
		this.prefSize = prefSize;
	}

	protected void setRelativeRect(Rectangle rect)
	{
		this.absoluteRect.size.width = rect.size.width;
		this.absoluteRect.size.height = rect.size.height;
		this.relativeRect = rect;
	}
	
	protected boolean isPointInside(double x, double y)
	{
		return this.getRect().isPointInside(x, y);
	}
}

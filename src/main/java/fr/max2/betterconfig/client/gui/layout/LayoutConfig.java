package fr.max2.betterconfig.client.gui.layout;

public abstract class LayoutConfig<Param>
{
	public Size sizeOverride = new Size();
	public Padding outerPadding = new Padding();
	
	public abstract Size measureLayout(Param param);
	
	public abstract Rectangle computeLayout(Rectangle availableRect, Param param);
	
	protected Size getParentRequiredSize(Size childSize)
	{
		return this.outerPadding.unpad(childSize);
	}
	
	protected Rectangle getChildRect(Rectangle parentAvailableRect)
	{
		return this.outerPadding.pad(parentAvailableRect);
	}
}

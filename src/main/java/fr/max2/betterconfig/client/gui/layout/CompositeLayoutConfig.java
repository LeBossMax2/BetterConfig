package fr.max2.betterconfig.client.gui.layout;

import java.util.List;

import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.ICompositeComponent;

public class CompositeLayoutConfig extends LayoutConfig<ICompositeComponent>
{
	public Axis dir = Axis.VERTICAL;
	public int spacing = 0;
	public Padding innerPadding = new Padding();
	// justification, alignment
	
	@Override
	public Size measureLayout(ICompositeComponent component)
	{
		List<? extends IComponent> children = component.getChildren();
		Size innerSize = new Size();
		innerSize.set(this.dir, children.isEmpty() ? 0 : (children.size() - 1) * this.spacing);
		
		children.forEach(child -> innerSize.combine(child.measureLayout(), this.dir));

		Size size = this.innerPadding.unpad(innerSize);
		
		size.combine(this.sizeOverride, null);
		
		return this.getParentRequiredSize(size);
	}

	@Override
	public Rectangle computeLayout(Rectangle availableRect, ICompositeComponent component)
	{
		List<? extends IComponent> children = component.getChildren();
		Rectangle rect = this.getChildRect(availableRect);
		Rectangle innerRect = this.innerPadding.pad(rect);
		
		Axis crossDir = this.dir.perpendicular();
		
		int constrainedSize = 0;
		int constrainedChildCount = 0;
		int childCount = children.size();
		
		for (IComponent child : children)
		{
			int childMainSize = child.getPrefSize().get(this.dir);
			if (Size.isConstrained(childMainSize))
			{
				constrainedSize += childMainSize;
				constrainedChildCount++;
			}
		}
		
		int totalSpacing = children.isEmpty() ? 0 : (children.size() - 1) * this.spacing;
		int unconstrainedSize = innerRect.size.get(this.dir) - constrainedSize - totalSpacing;
		if (unconstrainedSize < 0) unconstrainedSize = 0;
		int unconstrainedChildCount = childCount - constrainedChildCount;
		if (unconstrainedChildCount < 0) unconstrainedChildCount = 0;
		
		int mainPos = innerRect.getPos(this.dir);
		int crossPos = innerRect.getPos(crossDir);
		
		for (IComponent child : children)
		{
			Size childPrefSize = child.getPrefSize();
			int childMainSize = childPrefSize.get(this.dir);
			int childCrossSize = childPrefSize.get(crossDir);
			
			if (!Size.isConstrained(childMainSize))
			{
				childMainSize = unconstrainedSize / unconstrainedChildCount;
				unconstrainedSize -= childMainSize;
				unconstrainedChildCount--;
			}
			if (!Size.isConstrained(childCrossSize))
			{
				childCrossSize = innerRect.size.get(crossDir);
			}
			
			Rectangle childRect = new Rectangle();
			
			childRect.size.set(this.dir, childMainSize);
			childRect.size.set(crossDir, childCrossSize);
			
			childRect.setPos(this.dir, mainPos);
			childRect.setPos(crossDir, crossPos); // TODO [#2] Layout justification
			
			mainPos += childMainSize + this.spacing;
			
			child.computeLayout(childRect);
		}
		
		return rect;
	}
}

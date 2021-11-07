package fr.max2.betterconfig.client.gui.layout;

import java.util.List;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.ICompositeComponent;
import fr.max2.betterconfig.client.gui.style.StyleProperty;
import net.minecraft.resources.ResourceLocation;

public enum CompositeLayoutConfig implements ILayoutConfig<ICompositeComponent>
{
	INSTANCE;
	
	public static StyleProperty<Axis> DIR = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "dir"), Axis.VERTICAL);
	public static StyleProperty<Integer> SPACING = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "spacing"), 0);
	public static StyleProperty<Padding> INNER_PADDING = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "inner_padding"), new Padding());
	// justification, alignment
	
	@Override
	public Size measureLayout(ICompositeComponent component)
	{
		Axis dir = component.getStyleProperty(DIR);
		List<? extends IComponent> children = component.getChildren();
		Size innerSize = new Size();
		innerSize.set(dir, children.isEmpty() ? 0 : (children.size() - 1) * component.getStyleProperty(SPACING));
		
		children.forEach(child -> innerSize.combine(child.measureLayout(), dir));

		Size size = component.getStyleProperty(INNER_PADDING).unpad(innerSize);
		
		size.combine(component.getStyleProperty(ComponentLayoutConfig.SIZE_OVERRIDE), null);
		
		return ComponentLayoutConfig.getParentRequiredSize(component, size);
	}

	@Override
	public Rectangle computeLayout(Rectangle availableRect, ICompositeComponent component)
	{
		Axis dir = component.getStyleProperty(DIR);
		int spacing = component.getStyleProperty(SPACING);
		List<? extends IComponent> children = component.getChildren();
		Rectangle rect = ComponentLayoutConfig.getChildRect(component, availableRect);
		Rectangle innerRect = component.getStyleProperty(INNER_PADDING).pad(rect);
		
		Axis crossDir = dir.perpendicular();
		
		int constrainedSize = 0;
		int constrainedChildCount = 0;
		int childCount = children.size();
		
		for (IComponent child : children)
		{
			int childMainSize = child.getPrefSize().get(dir);
			if (Size.isConstrained(childMainSize))
			{
				constrainedSize += childMainSize;
				constrainedChildCount++;
			}
		}
		
		int totalSpacing = children.isEmpty() ? 0 : (children.size() - 1) * spacing;
		int unconstrainedSize = innerRect.size.get(dir) - constrainedSize - totalSpacing;
		if (unconstrainedSize < 0) unconstrainedSize = 0;
		int unconstrainedChildCount = childCount - constrainedChildCount;
		if (unconstrainedChildCount < 0) unconstrainedChildCount = 0;
		
		int mainPos = innerRect.getPos(dir);
		int crossPos = innerRect.getPos(crossDir);
		
		for (IComponent child : children)
		{
			Size childPrefSize = child.getPrefSize();
			int childMainSize = childPrefSize.get(dir);
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
			
			childRect.size.set(dir, childMainSize);
			childRect.size.set(crossDir, childCrossSize);
			
			childRect.setPos(dir, mainPos);
			childRect.setPos(crossDir, crossPos); // TODO [#2] Layout justification
			
			mainPos += childMainSize + spacing;
			
			child.computeLayout(childRect);
		}
		
		return rect;
	}
}

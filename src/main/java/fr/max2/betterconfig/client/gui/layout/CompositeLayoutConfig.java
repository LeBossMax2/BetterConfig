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
	
	public static final StyleProperty<Axis> DIR = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "dir"), Axis.VERTICAL);
	public static final StyleProperty<Integer> SPACING = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "spacing"), 0);
	public static final StyleProperty<Padding> INNER_PADDING = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "inner_padding"), new Padding());
	public static final StyleProperty<Alignment> JUSTIFICATION = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "justification"), Alignment.MIN);
	public static final StyleProperty<Alignment> ALIGNMENT = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "alignment"), Alignment.MIN);
	
	private static List<? extends IComponent> getLayoutChildren(ICompositeComponent component)
	{
		return component.getChildren().stream().filter(child -> !child.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed()).toList();
	}
	
	@Override
	public Size measureLayout(ICompositeComponent component)
	{
		Axis dir = component.getStyleProperty(DIR);
		List<? extends IComponent> children = getLayoutChildren(component);
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
		List<? extends IComponent> children = getLayoutChildren(component);
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
		
		if (unconstrainedChildCount == 0)
		{
			mainPos += component.getStyleProperty(ALIGNMENT).getOffset(unconstrainedSize);
		}
		
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
			int childCrossPos = crossPos;
			if (Size.isConstrained(childCrossSize))
			{
				childCrossPos += component.getStyleProperty(JUSTIFICATION).getOffset(innerRect.size.get(crossDir) - childCrossSize);
			}
			else
			{
				childCrossSize = innerRect.size.get(crossDir);
			}
			
			Rectangle childRect = new Rectangle();
			
			childRect.size.set(dir, childMainSize);
			childRect.size.set(crossDir, childCrossSize);
			
			childRect.setPos(dir, mainPos);
			childRect.setPos(crossDir, childCrossPos);
			
			mainPos += childMainSize + spacing;
			
			child.computeLayout(childRect);
		}
		
		return rect;
	}
}

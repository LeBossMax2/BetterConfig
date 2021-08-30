package fr.max2.betterconfig.client.gui.component;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;

/**
 * Manages the layout of a user interface
 */
public interface IComponentParent
{
	/**
	 * Marks the layout dirty in order to update the layout
	 */
	void marksLayoutDirty();
	
	/**
	 * Gets the x coordinate of the start of the layout
	 */
	int getLayoutX();

	/**
	 * Gets the x coordinate of the start of the layout
	 */
	int getLayoutY();
	
	/**
	 * Register the given action to be ran at the end of the current GUI event
	 * @param action the action to run
	 */
	void enqueueWork(Runnable action);
	
	default Size updateLayout(IComponent component, Size availableSize)
	{
		Size size = component.measureLayout();
		Rectangle componentRect = new Rectangle(0, 0, availableSize.width, availableSize.height);
		componentRect.size.combine(size, null);
		component.computeLayout(componentRect);
		return componentRect.size;
	}
}

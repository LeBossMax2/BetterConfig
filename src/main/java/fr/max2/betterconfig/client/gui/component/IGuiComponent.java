package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * Represents a component of user interface
 */
public interface IGuiComponent extends Widget, GuiEventListener
{
	/**
	 * Renders the overlay of the element if it has one
	 * @param matrixStack the transformation matrix stack
	 * @param mouseX the x coordinate of the mouse on the screen
	 * @param mouseY the y coordinate of the mouse on the screen
	 * @param partialTicks
	 */
	default void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
	void setLayoutManager(ILayoutManager manager);
	
	/**
	 * Gets the width of the component
	 */
	int getWidth();
	
	/**
	 * Gets the height of the component
	 */
	int getHeight();
	
	/**
	 * Called when the layout manager changes
	 */
	default void onLayoutChanged()
	{ }
	
	default void invalidate()
	{ }
}

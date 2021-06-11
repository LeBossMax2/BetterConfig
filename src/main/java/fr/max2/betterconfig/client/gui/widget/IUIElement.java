package fr.max2.betterconfig.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;

/**
 * Represents a component of user interface
 */
public interface IUIElement extends IRenderable, IGuiEventListener
{
	/**
	 * Renders the overlay of the element if it has one
	 * @param matrixStack the transformation matrix stack
	 * @param mouseX the x coordinate of the mouse on the screen
	 * @param mouseY the y coordinate of the mouse on the screen
	 * @param partialTicks
	 */
	default void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
}

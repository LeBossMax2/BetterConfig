package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import net.minecraft.client.gui.components.Widget;

/**
 * Represents a component of user interface
 */
// TODO [#2] Handle narration
public interface IComponent extends Widget
{
	// Layout
	
	Size getPrefSize();
	
	Size measureLayout();
	
	void computeLayout(Rectangle availableRect);
	
	void invalidate();
	
	// Rendering
	
	/**
	 * Renders the overlay of the element if it has one
	 * @param matrixStack the transformation matrix stack
	 * @param mouseX the x coordinate of the mouse on the screen
	 * @param mouseY the y coordinate of the mouse on the screen
	 * @param partialTicks
	 */
	void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);
	
	// Input handling
	
	void mouseMoved(double mouseX, double mouseY);
	
	void mouseClicked(double mouseX, double mouseY, int button, EventState state);
	
	void mouseReleased(double mouseX, double mouseY, int button, EventState state);
	
	void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state);
	
	void mouseScrolled(double mouseX, double mouseY, double delta, EventState state);
	
	void keyPressed(int keyCode, int scanCode, int modifiers, EventState state);
	
	void keyReleased(int keyCode, int scanCode, int modifiers, EventState state);
	
	void charTyped(char codePoint, int modifiers, EventState state);
	
	void cycleFocus(boolean forward, CycleFocusState state);
}

package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Represents a component of user interface composed of other component
 */
public interface ICompositeComponent extends IComponent
{
	// Layout

	/**
	 * Gets the list of children user interface components
	 */
	List<? extends IComponent> getChildren();
	
	@Override
	default void init(IComponentParent layoutManager, IComponent parent)
	{
		for (IComponent elem : this.getChildren())
		{
			elem.init(layoutManager, this);
		}
	}
	
	@Override
	default void invalidate()
	{
		for (IComponent elem : this.getChildren())
		{
			elem.invalidate();
		}
	}
	
	// Rendering

	@Override
	default void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IComponent elem : this.getChildren())
		{
			elem.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	default void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state)
	{
		for (IComponent elem : this.getChildren())
		{
			elem.renderOverlay(matrixStack, mouseX, mouseY, partialTicks, state);
		}
	}
	
	// Input handling
	
	@Override
	default void mouseMoved(double mouseX, double mouseY)
	{
		for (IComponent child : this.getChildren())
		{
			child.mouseMoved(mouseX, mouseY);
		}
	}
	
	@Override
	default void mouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.mouseClicked(mouseX, mouseY, button, state);
		}
	}
	
	@Override
	default void mouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.mouseReleased(mouseX, mouseY, button, state);
		}
	}
	
	@Override
	default void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.mouseDragged(mouseX, mouseY, button, dragX, dragY, state);
		}
	}
	
	@Override
	default void mouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.mouseScrolled(mouseX, mouseY, delta, state);
		}
	}
	
	@Override
	default void keyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.keyPressed(keyCode, scanCode, modifiers, state);
		}
	}
	
	@Override
	default void keyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.keyReleased(keyCode, scanCode, modifiers, state);
		}
	}
	
	@Override
	default void charTyped(char codePoint, int modifiers, EventState state)
	{
		for (IComponent child : this.getChildren())
		{
			child.charTyped(codePoint, modifiers, state);
		}
	}
	
	@Override
	default void cycleFocus(boolean forward, CycleFocusState state)
	{
		List<? extends IComponent> children = this.getChildren();
		if (!forward)
			children = Lists.reverse(children);
		
		for (IComponent child : children)
		{
			child.cycleFocus(forward, state);
		}
	}
}

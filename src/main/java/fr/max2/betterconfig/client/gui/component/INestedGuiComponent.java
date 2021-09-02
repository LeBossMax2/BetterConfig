package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.gui.components.events.ContainerEventHandler;

/**
 * Represents a component of user interface composed of other component
 */
public interface INestedGuiComponent extends IGuiComponent, ContainerEventHandler
{
	/**
	 * Gets the list of children user interface components
	 */
	@Override
	List<? extends IGuiComponent> children();
	
	// Layout
	
	@Override
	default void setLayoutManager(ILayoutManager manager)
	{
		for (IGuiComponent elem : this.children())
		{
			elem.setLayoutManager(manager);
		}
	}
	
	@Override
	default void onLayoutChanged()
	{
		for (IGuiComponent elem : this.children())
		{
			elem.onLayoutChanged();
		}
	}
	
	// Rendering

	@Override
	default void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IGuiComponent elem : this.children())
		{
			elem.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	default void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IGuiComponent elem : this.children())
		{
			elem.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	default void invalidate()
	{
		for (IGuiComponent elem : this.children())
		{
			elem.invalidate();
		}
	}
}

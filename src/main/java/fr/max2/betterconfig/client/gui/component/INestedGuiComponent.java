package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.gui.INestedGuiEventHandler;

/**
 * Represents a component of user interface composed of other component
 */
public interface INestedGuiComponent extends IGuiComponent, INestedGuiEventHandler
{
	/**
	 * Gets the list of children user interface components
	 */
	@Override
	List<? extends IGuiComponent> getEventListeners();
	
	// Layout
	
	@Override
	default void setLayoutManager(ILayoutManager manager)
	{
		for (IGuiComponent elem : this.getEventListeners())
		{
			elem.setLayoutManager(manager);
		}
	}
	
	@Override
	default void onLayoutChanged()
	{
		for (IGuiComponent elem : this.getEventListeners())
		{
			elem.onLayoutChanged();
		}
	}
	
	// Rendering

	@Override
	default void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IGuiComponent elem : this.getEventListeners())
		{
			elem.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	default void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IGuiComponent elem : this.getEventListeners())
		{
			elem.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
}

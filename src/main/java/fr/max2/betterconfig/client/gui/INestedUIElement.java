package fr.max2.betterconfig.client.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.INestedGuiEventHandler;

public interface INestedUIElement extends IUIElement, INestedGuiEventHandler
{
	@Override
	List<? extends IUIElement> getEventListeners();

	@Override
	default void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IUIElement elem : this.getEventListeners())
		{
			elem.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	default void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		for (IUIElement elem : this.getEventListeners())
		{
			elem.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
}

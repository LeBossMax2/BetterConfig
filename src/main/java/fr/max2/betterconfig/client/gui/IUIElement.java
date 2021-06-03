package fr.max2.betterconfig.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;

public interface IUIElement extends IRenderable, IGuiEventListener
{
	void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
}

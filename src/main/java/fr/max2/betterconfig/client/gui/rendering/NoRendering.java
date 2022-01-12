package fr.max2.betterconfig.client.gui.rendering;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Rectangle;

public class NoRendering implements IRenderLayer
{
	@Override
	public String typeName()
	{
		return "none";
	}

	@Override
	public void renderLayer(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{ }
}

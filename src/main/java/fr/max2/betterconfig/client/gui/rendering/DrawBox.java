package fr.max2.betterconfig.client.gui.rendering;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Rectangle;

public class DrawBox implements IRenderLayer
{
	private Padding positioning;
	private IMaterial material;

	public DrawBox(Padding positioning, IMaterial material)
	{
		this.positioning = positioning;
		this.material = material;
	}

	@Override
	public void renderLayer(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		this.material.renderMaterial(this.positioning.pad(rect), poseStack, mouseX, mouseY, partialTick);
	}

	@Override
	public String typeName()
	{
		return "box";
	}
}

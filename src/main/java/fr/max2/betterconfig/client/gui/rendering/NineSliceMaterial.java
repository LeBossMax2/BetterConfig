package fr.max2.betterconfig.client.gui.rendering;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmlclient.gui.GuiUtils;

public class NineSliceMaterial implements IMaterial
{
	private final ResourceLocation atlasId;
	private final int u, v;
	private final int textureWidth, textureHeight;
	private final int topBorder, bottomBorder, leftBorder, rightBorder;

	public NineSliceMaterial(ResourceLocation atlasId,
			int u, int v,
			int textureWidth, int textureHeight,
			int topBorder, int bottomBorder, int leftBorder, int rightBorder)
	{
		this.atlasId = atlasId;
		this.u = u;
		this.v = v;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.topBorder = topBorder;
		this.bottomBorder = bottomBorder;
		this.leftBorder = leftBorder;
		this.rightBorder = rightBorder;
	}

	@Override
	public void renderMaterial(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		GuiUtils.drawContinuousTexturedBox(poseStack, this.atlasId,
				rect.x, rect.y, this.u, this.v,
				rect.size.width, rect.size.height, this.textureWidth, this.textureHeight,
				this.topBorder, this.bottomBorder, this.leftBorder, this.rightBorder, 0.0f);
	}

	@Override
	public String typeName()
	{
		return "9-slice";
	}
}

package fr.max2.betterconfig.client.gui.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class TextureMaterial implements IMaterial
{
	private final ResourceLocation atlasId;
	private final float minU, maxU, minV, maxV;
	
	// 0 ~ 1 range
	public TextureMaterial(ResourceLocation atlasId, float minU, float minV, float maxU, float maxV)
	{
		this.atlasId = atlasId;
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}

	// 0 ~ textureWidth, 0 ~ textureHeight range
	public TextureMaterial(ResourceLocation atlasId, float minU, float minV, float widthU, float heightV, float textureWidth, float textureHeight)
	{
		this(atlasId, minU / textureWidth, minV / textureHeight, widthU / textureWidth, heightV / textureHeight);
	}

	// 0 ~ 16 range
	public TextureMaterial(TextureAtlasSprite sprite, float minU, float minV, float maxU, float maxV)
	{
		this(sprite.atlas().location(), sprite.getU(minU), sprite.getV(minV), sprite.getU(maxU), sprite.getV(maxV));
	}

	@Override
	public void renderMaterial(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
        RenderSystem.setShaderTexture(0, this.atlasId);
		Matrix4f matrix = poseStack.last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertex(bufferbuilder, matrix, rect.getLeft(), rect.getBottom(), this.minU, this.maxV);
		vertex(bufferbuilder, matrix, rect.getRight(), rect.getBottom(), this.maxU, this.maxV);
		vertex(bufferbuilder, matrix, rect.getRight(), rect.getTop(), this.maxU, this.minV);
		vertex(bufferbuilder, matrix, rect.getLeft(), rect.getTop(), this.minU, this.minV);
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}

	@Override
	public String typeName()
	{
		return "texture";
	}

	private static void vertex(BufferBuilder bufferbuilder, Matrix4f matrix, float x, float y, float u, float v)
	{
		bufferbuilder.vertex(matrix, x, y, 0.0F).uv(u, v).endVertex();
	}
}

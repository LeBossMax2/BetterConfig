package fr.max2.betterconfig.client.gui.better;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

public class BetterButton extends Button
{
	/** The parent screen */
	protected final BetterConfigScreen screen;
	
	private final List<? extends FormattedText> tooltipInfo;
	
	public BetterButton(BetterConfigScreen screen, IComponentParent layoutManager, int width, Component displayString, OnPress pressedHandler, Component overlay)
	{
		super(layoutManager, displayString, pressedHandler);
		this.addClass("better:button");
		this.screen = screen;
		this.tooltipInfo = Arrays.asList(overlay);
		this.setStyle(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(width, VALUE_HEIGHT));
	}
	
	@Override
	public void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.isPointInside(mouseX, mouseY))
		{
			Font font = Minecraft.getInstance().font;
			GuiUtils.drawHoveringText(matrixStack, this.tooltipInfo, mouseX, mouseY, this.screen.width, this.screen.height, 200, font);
		}
	}

	public static class Icon extends BetterButton
	{
		private final int iconU;
		private final int iconV;
	
		public Icon(BetterConfigScreen screen, IComponentParent layoutManager, int iconU, int iconV, Component displayString, OnPress pressedHandler, Component overlay)
		{
			super(screen, layoutManager, VALUE_HEIGHT, displayString, pressedHandler, overlay);
			this.iconU = iconU;
			this.iconV = iconV;
		}
		
		@Override
		public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
		{
			// Draw foreground icon
			int v = this.iconV + (this.widget.isHovered() ? 16 : 0);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
	        RenderSystem.setShaderTexture(0, BETTER_ICONS);
	        Rectangle rect = this.getRect();
			blit(mStack, rect.x + (rect.size.width - 16) / 2, rect.y + (rect.size.height - 16) / 2, this.iconU, v, 16, 16, 256, 256);
		}
		
	}
}
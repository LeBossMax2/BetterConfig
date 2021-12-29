package fr.max2.betterconfig.client.gui.better;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.client.gui.component.widget.TextOverlay;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

public class BetterButton extends Button
{
	/** The parent screen */
	protected final BetterConfigScreen screen;
	
	public BetterButton(BetterConfigScreen screen, int width, Component displayString, OnPress pressedHandler, Component overlay)
	{
		super(displayString, pressedHandler, new TextOverlay(screen, overlay));
		this.addClass("better:button");
		this.screen = screen;
		this.setStyle(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(width, VALUE_HEIGHT));
	}

	public static class Icon extends BetterButton
	{
		private final int iconU;
		private final int iconV;
	
		public Icon(BetterConfigScreen screen, int iconU, int iconV, Component displayString, OnPress pressedHandler, Component overlay)
		{
			super(screen, VALUE_HEIGHT, displayString, pressedHandler, overlay);
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
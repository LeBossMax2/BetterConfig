package fr.max2.betterconfig.client.gui.better;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.client.gui.component.widget.TextOverlay;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;


public class BetterButton extends Button
{
	public BetterButton(BetterConfigScreen screen, Component displayString, Component overlay)
	{
		super(displayString, new TextOverlay(screen, overlay));
		this.addClass("better:button");
	}
	
	public BetterButton(Component displayString)
	{
		super(displayString, NO_OVERLAY);
		this.addClass("better:button");
	}

	public static class Icon extends BetterButton
	{
		private final int iconU;
		private final int iconV;
	
		public Icon(BetterConfigScreen screen, int iconU, int iconV, Component displayString, Component overlay)
		{
			super(screen, displayString, overlay);
			this.iconU = iconU;
			this.iconV = iconV;
			this.addClass("better:icon_button");
		}
		
		@Override
		protected void onRender(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
		{
			// Draw foreground icon
			int v = this.iconV + (this.isHovered() ? 16 : 0);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
	        RenderSystem.setShaderTexture(0, Constants.BETTER_ICONS);
	        Rectangle rect = this.getRect();
			blit(pPoseStack, rect.x + (rect.size.width - 16) / 2, rect.y + (rect.size.height - 16) / 2, this.iconU, v, 16, 16, 256, 256);
		}
		
	}
}
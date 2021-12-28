package fr.max2.betterconfig.client.gui.component.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button.OnTooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

/**
 * A basic button
 */
public class Button extends WidgetComponent<Button.InnerButton>
{
	public static final OnTooltip NO_TOOLTIP = net.minecraft.client.gui.components.Button.NO_TOOLTIP;
	/** The overlay to show when the mouse is over the button */
	private final OnTooltip overlay;

	public Button(Component displayString, OnPress pressedHandler, OnTooltip overlay)
	{
		super("button", new InnerButton(0, 0, 0, 0, displayString, pressedHandler));
		this.overlay = overlay;
		this.widget.parent = this;
	}

	public Button(Component displayString, OnPress pressedHandler)
	{
		this(displayString, pressedHandler, NO_TOOLTIP);
	}
	
	public void setMessage(Component message)
	{
		this.widget.setMessage(message);
	}
	
	// Rendering
	
	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.overlay.onTooltip(this.widget, matrixStack, mouseX, mouseY);
	}
	
	protected void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
	{
		this.widget.superRenderButton(mStack, mouseX, mouseY, partial);
	}
	
	// Narration
	
	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		super.updateNarration(narrationOutput);
	      this.overlay.narrateTooltip((text) -> {
	    	  narrationOutput.add(NarratedElementType.HINT, text);
	       });
	}
	
	@FunctionalInterface
	public static interface OnPress
	{
		void onPress(Button button);
	}

	public static class InnerButton extends ExtendedButton
	{
		private final Button.OnPress handler;
		private Button parent;
		
		private InnerButton(int xPos, int yPos, int width, int height, Component displayString, Button.OnPress handler)
		{
			super(xPos, yPos, width, height, displayString, null);
			this.handler = handler;
		}
		
		// Rendering
		
		@Override
		public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
		{
			// Fix rendering bug
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			this.parent.renderButton(mStack, mouseX, mouseY, partial);
		}
		
		private void superRenderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
		{
			super.renderButton(mStack, mouseX, mouseY, partial);
		}
		
		@Override
		public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY)
		{
			// Disable default tooltip overlay
		}
		
		// Input handling
		
		@Override
		public void onPress()
		{
			this.parent.layoutManager.enqueueWork(() -> this.handler.onPress(this.parent));
		}
		
		@Override
		public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
		{
			if (!this.clicked(pMouseX, pMouseY))
				this.setFocused(false);
			return super.mouseClicked(pMouseX, pMouseY, pButton);
		}
		
		@Override
		public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
		{
			if (!this.isFocused())
				return false;
			return super.keyPressed(pKeyCode, pScanCode, pModifiers);
		}

		@Override
		public boolean isHovered()
		{
			return super.isHovered();
		}
	}
}

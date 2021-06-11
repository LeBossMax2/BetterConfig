package fr.max2.betterconfig.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

/**
 * A basic button
 */
public class Button extends ExtendedButton implements IUIElement 
{
	/** An empty overlay */
	public static final Button.ITooltip NO_TOOLTIP = EMPTY_TOOLTIP;

	/** The overlay to show when the mouse is over the button */
	private final ITooltip overlay;

	public Button(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable pressedHandler, ITooltip overlay)
	{
		super(xPos, yPos, width, height, displayString, pressedHandler);
		this.overlay = overlay;
	}

	public Button(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable pressedHandler)
	{
		this(xPos, yPos, width, height, displayString, pressedHandler, NO_TOOLTIP);
	}
	
	@Override
	public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		// Disable default tooltip overlay
	}
	
	@Override
	public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.overlay.onTooltip(this, matrixStack, mouseX, mouseY);
	}
}

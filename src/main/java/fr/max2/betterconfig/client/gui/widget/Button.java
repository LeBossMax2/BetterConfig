package fr.max2.betterconfig.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class Button extends ExtendedButton implements IUIElement 
{
	public static final Button.ITooltip NO_TOOLTIP = EMPTY_TOOLTIP;

	private final ITooltip overlay;

	public Button(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler, ITooltip overlay)
	{
		super(xPos, yPos, width, height, displayString, handler);
		this.overlay = overlay;
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

package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

/**
 * A basic button
 */
public class Button extends ExtendedButton implements IGuiComponent 
{
	/** The overlay to show when the mouse is over the button */
	private final OnTooltip overlay;
	/** The parent layout */
	private ILayoutManager layout = ILayoutManager.NONE;
	/** The x coordinate relative to the layout */
	protected int baseX;
	/** The y coordinate relative to the layout */
	protected int baseY;

	public Button(int x, int y, int width, int height, Component displayString, OnPress pressedHandler, OnTooltip overlay)
	{
		super(x, y, width, height, displayString, pressedHandler);
		this.baseX = x;
		this.baseY = y;
		this.overlay = overlay;
	}

	public Button(int xPos, int yPos, int width, int height, Component displayString, OnPress pressedHandler)
	{
		this(xPos, yPos, width, height, displayString, pressedHandler, NO_TOOLTIP);
	}
	
	// Layout
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		this.onLayoutChanged();
	}
	
	@Override
	public void onLayoutChanged()
	{
		this.x = this.baseX + this.layout.getLayoutX();
		this.y = this.baseY + this.layout.getLayoutY();
	}
	
	/** Sets the x position of this button relative to the layout position */
	public void setX(int x)
	{
		this.baseX = x;
		this.onLayoutChanged();
	}

	/** Sets the y position of this button relative to the layout position */
	public void setY(int y)
	{
		this.baseY = y;
		this.onLayoutChanged();
	}
	
	// Rendering
	
	@Override
	public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		super.renderButton(mStack, mouseX, mouseY, partial);
	}
	
	@Override
	public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY)
	{
		// Disable default tooltip overlay
	}
	
	@Override
	public void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.overlay.onTooltip(this, matrixStack, mouseX, mouseY);
	}
}

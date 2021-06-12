package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

/**
 * A basic button
 */
public class Button extends ExtendedButton implements IGuiComponent 
{
	/** An empty overlay */
	public static final Button.ITooltip NO_TOOLTIP = EMPTY_TOOLTIP;

	/** The overlay to show when the mouse is over the button */
	private final ITooltip overlay;
	/** The parent layout */
	private ILayoutManager layout = ILayoutManager.NONE;
	/** The x coordinate relative to the layout */
	protected int baseX;
	/** The y coordinate relative to the layout */
	protected int baseY;

	public Button(int x, int y, int width, int height, ITextComponent displayString, IPressable pressedHandler, ITooltip overlay)
	{
		super(x, y, width, height, displayString, pressedHandler);
		this.baseX = x;
		this.baseY = y;
		this.overlay = overlay;
	}

	public Button(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable pressedHandler)
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

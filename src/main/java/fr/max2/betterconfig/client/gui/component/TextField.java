package fr.max2.betterconfig.client.gui.component;

import org.lwjgl.glfw.GLFW;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * A widget for entering text
 */
public class TextField extends EditBox implements IGuiComponent
{
	/** The parent layout */
	private ILayoutManager layout = ILayoutManager.NONE;
	/** The x coordinate relative to the layout */
	protected int baseX;
	/** The y coordinate relative to the layout */
	protected int baseY;
	
	public TextField(Font fontRenderer, int x, int y, int width, int height, Component title)
	{
		super(fontRenderer, x, y, width, height, title);
		this.setMaxLength(Integer.MAX_VALUE);
		this.baseX = x;
		this.baseY = y;
	}
	
	@Override
	public void setValue(String text)
	{
		super.setValue(text == null ? "" : text);
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
	@Override
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
	
	@Override
	protected void setFocused(boolean focused)
	{
		if (this.isFocused() != focused)
		{
			super.setFocused(focused);
			this.onFocusedChanged(focused);
		}
	}
	
	@Override
	public void setFocus(boolean focused)
	{
		if (this.isFocused() != focused)
		{
			super.setFocus(focused);
			this.onFocusedChanged(focused);
		}
	}
	
	@Override
	protected void onFocusedChanged(boolean focused)
	{
		super.onFocusedChanged(focused);
		if (!focused)
		{
			this.onValidate(this.getValue());
		}
	}
	
	// Input handling
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (!this.canConsumeInput())
			return false;
		if (super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		
		switch (keyCode)
		{
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_KP_ENTER:
			this.onValidate(this.getValue());
			return true;
			
		default:
			return false;
		}
	}

	/**
	 * A function called when a new value is validated by the user.
	 * This is called when the user presses enter or unfocuses the widget
	 */
	protected void onValidate(String text)
	{ }
}

package fr.max2.betterconfig.client.gui.component;

import org.lwjgl.glfw.GLFW;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

/**
 * A widget for entering text
 */
public class TextField extends TextFieldWidget implements IGuiComponent
{
	/** The parent layout */
	private ILayoutManager layout = ILayoutManager.NONE;
	/** The x coordinate relative to the layout */
	protected int baseX;
	/** The y coordinate relative to the layout */
	protected int baseY;
	
	public TextField(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent title)
	{
		super(fontRenderer, x, y, width, height, title);
		this.setMaxStringLength(Integer.MAX_VALUE);
		this.baseX = x;
		this.baseY = y;
	}
	
	@Override
	public void setText(String text)
	{
		super.setText(text == null ? "" : text);
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
	public void setFocused2(boolean focused)
	{
		if (this.isFocused() != focused)
		{
			super.setFocused2(focused);
			this.onFocusedChanged(focused);
		}
	}
	
	@Override
	protected void onFocusedChanged(boolean focused)
	{
		super.onFocusedChanged(focused);
		if (!focused)
		{
			this.onValidate(this.getText());
		}
	}
	
	// Input handling
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (!this.canWrite())
			return false;
		if (super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		
		switch (keyCode)
		{
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_KP_ENTER:
			this.onValidate(this.getText());
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

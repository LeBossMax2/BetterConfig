package fr.max2.betterconfig.client.gui.widget;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

/**
 * A widget for entering text
 */
public class TextField extends TextFieldWidget implements IUIElement
{
	public TextField(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent title)
	{
		super(fontRenderer, x, y, width, height, title);
	}
	
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

	/**
	 * A function called when a new value is validated by the user.
	 * This is called when the user presses enter or unfocuses the widget
	 */
	protected void onValidate(String text)
	{ }
}

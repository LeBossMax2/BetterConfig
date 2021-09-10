package fr.max2.betterconfig.client.gui.component.widget;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.UnitLayoutConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * A widget for entering text
 */
public class TextField extends WidgetComponent<EditBox>
{
	public final UnitLayoutConfig config = new UnitLayoutConfig();
	
	public TextField(IComponentParent layoutManager, Font fontRenderer, Component title)
	{
		super(layoutManager, new InnerField(fontRenderer, title));
		((InnerField)this.widget).parent = this;
		this.widget.setMaxLength(Integer.MAX_VALUE);
		this.config.outerPadding = new Padding(1, 1, 1, 1);
	}
	
	public Component getMessage()
	{
		return this.widget.getMessage();
	}
	
	public String getValue()
	{
		return this.widget.getValue();
	}
	
	public void setValue(String value)
	{
		this.widget.setValue(value);
	}
	
	public void setResponder(Consumer<String> responder)
	{
		this.widget.setResponder(responder);
	}
	
	public void setTextColor(int color)
	{
		this.widget.setTextColor(color);
	}
	
	// Input handling
	
	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		if (state.isConsumed() || !this.widget.canConsumeInput())
			return;
		super.keyPressed(keyCode, scanCode, modifiers, state);
		if (state.isConsumed())
			return;
		
		switch (keyCode)
		{
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_KP_ENTER:
			this.onValidate(this.widget.getValue());
			state.consume();
			break;
		default:
			break;
		}
	}

	@Override
	protected UnitLayoutConfig getLayoutConfig()
	{
		return this.config;
	}

	/**
	 * A function called when a new value is validated by the user.
	 * This is called when the user presses enter or unfocuses the widget
	 */
	protected void onValidate(String text)
	{ }
	
	private static class InnerField extends EditBox
	{
		private TextField parent;

		public InnerField(Font pFont, Component pMessage)
		{
			super(pFont, 0, 0, 100000, 0, pMessage);
		}
		
		@Override
		public void setValue(String text)
		{
			super.setValue(text == null ? "" : text);
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
				this.parent.onValidate(this.getValue());
			}
		}
		
	}
}

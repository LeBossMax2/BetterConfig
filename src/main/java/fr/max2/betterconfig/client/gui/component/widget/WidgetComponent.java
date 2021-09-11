package fr.max2.betterconfig.client.gui.component.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.component.CycleFocusState;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import net.minecraft.client.gui.components.AbstractWidget;

public abstract class WidgetComponent<W extends AbstractWidget> extends UnitComponent
{
	public final W widget;

	public WidgetComponent(IComponentParent layoutManager, String type, W widget)
	{
		super(layoutManager, type);
		this.widget = widget;
	}
	
	private void updatePosition()
	{
		this.widget.x = this.getRect().x;
		this.widget.y = this.getRect().y;
	}
	
	@Override
	protected void setRelativeRect(Rectangle rect)
	{
		super.setRelativeRect(rect);
		this.widget.setWidth(rect.size.width);
		this.widget.setHeight(rect.size.height);
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		updatePosition();
		this.widget.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		updatePosition();
		this.widget.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public void mouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		updatePosition();
		if (this.widget.mouseClicked(mouseX, mouseY, button))
			state.consume();
	}
	
	@Override
	public void mouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		updatePosition();
		if (!state.isConsumed() && this.widget.mouseReleased(mouseX, mouseY, button))
			state.consume();
	}
	
	@Override
	public void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		updatePosition();
		if (!state.isConsumed() && this.widget.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			state.consume();
	}
	
	@Override
	public void mouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		updatePosition();
		if (!state.isConsumed() && this.widget.mouseScrolled(mouseX, mouseY, delta))
			state.consume();
	}
	
	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		updatePosition();
		if (!state.isConsumed() && this.widget.isFocused() && this.widget.keyPressed(keyCode, scanCode, modifiers))
			state.consume();
	}
	
	@Override
	public void keyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		updatePosition();
		if (!state.isConsumed() && this.widget.isFocused() && this.widget.keyReleased(keyCode, scanCode, modifiers))
			state.consume();
	}
	
	@Override
	public void charTyped(char codePoint, int modifiers, EventState state)
	{
		updatePosition();
		if (!state.isConsumed() && this.widget.isFocused() && this.widget.charTyped(codePoint, modifiers))
			state.consume();
	}
	
	@Override
	public void cycleFocus(boolean forward, CycleFocusState state)
	{
		updatePosition();
		if (state.isConsumed())
		{
			// If something is already focused
			if (this.widget.isFocused())
			{
				// Ensure this widget is not focused
				this.widget.changeFocus(forward);
			}
		}
		else if (state.isPropagated())
		{
			// Ensure focused
			if (this.widget.isFocused())
			{
				state.consume();
			}
			else if (this.widget.changeFocus(forward))
			{
				state.consume();
			}
		}
		else if (this.widget.isFocused())
		{
			// Try unfocus and propagate
			if (this.widget.changeFocus(forward))
			{
				state.consume();
			}
			else
			{
				state.propagate();
			}
		}
	}
	
}

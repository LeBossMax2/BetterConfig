package fr.max2.betterconfig.client.gui.component.widget;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.component.CycleFocusState;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.util.Event;
import fr.max2.betterconfig.util.IEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;

/**
 * A basic button
 */
public class Button extends UnitComponent
{
	private Component message;
	private Event<OnPress> onPressed = new Event<>();
	private boolean isActive = true;

	public Button(Component displayString, OnTooltip overlay)
	{
		super("button");
		this.message = displayString;
		this.overlay = overlay;
		this.registerProperty(WidgetComponent.ACTIVE, () -> this.active());
	}

	public Button(Component displayString)
	{
		this(displayString, NO_OVERLAY);
	}

	public void setMessage(Component message)
	{
		this.message = message;
	}

	public Component getMessage()
	{
		return this.message;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public boolean active()
	{
		return this.isActive;
	}

	@Override
	public boolean isActive()
	{
		return super.isActive() && this.active();
	}

	public IEvent<OnPress> onPressed()
	{
		return this.onPressed;
	}

	public Button addOnPressed(OnPress handler)
	{
		this.onPressed.add(handler);
		return this;
	}

	// Rendering

	@Override
	protected void onRender(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
	{
		Font font = this.layoutManager.getMinecraft().font;
		Rectangle rect = this.getRect();
		Component text = this.getMessage();
		Style style = this.getStyleProperty(TEXT_STYLE);
		if (!style.equals(Style.EMPTY))
			text = text.copy().withStyle(style);
		drawCenteredString(pPoseStack, font, text, rect.getCenterX(), rect.getCenterY() - (font.lineHeight - 1) / 2, this.getStyleProperty(TEXT_COLOR));
	}

	// Input handling

	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		if (!this.isHovered() || state.isConsumed() || button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
			return;

		this.onPress();
		state.consume();
	}

	@Override
	protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		if (!this.hasFocus() || state.isConsumed())
			return;

		switch (keyCode)
		{
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_KP_ENTER:
		case GLFW.GLFW_KEY_SPACE:
			this.onPress();
			state.consume();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCycleFocus(boolean forward, CycleFocusState state)
	{
		this.cycleSelfFocus(state);
	}

	protected void onPress()
	{
		this.layoutManager.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		this.layoutManager.enqueueWork(() -> this.onPressed.call(OnPress::onPress));
	}

	// Narration

	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		narrationOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.button", this.getMessage()));
		if (this.active())
		{
			String state = this.hasFocus() ? "focused" : "hovered";
			narrationOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage." + state));
		}
		super.updateNarration(narrationOutput);
	}

	@FunctionalInterface
	public static interface OnPress
	{
		void onPress();
	}
}

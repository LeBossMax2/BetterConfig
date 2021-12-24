package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;

public abstract class CompositeComponent extends Component<ICompositeComponent> implements ICompositeComponent
{
	public CompositeComponent(IComponentParent layoutManager, String type)
	{
		super(layoutManager, type);
	}
	
	@Override
	protected CompositeLayoutConfig getLayoutConfig()
	{
		return CompositeLayoutConfig.INSTANCE;
	}

	@Override
	protected ICompositeComponent getLayoutParam()
	{
		return this;
	}
	
	// Rendering

	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		ICompositeComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		ICompositeComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	// Input handling
	
	@Override
	protected void onMouseMoved(double mouseX, double mouseY)
	{
		ICompositeComponent.super.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		ICompositeComponent.super.mouseClicked(mouseX, mouseY, button, state);
	}
	
	@Override
	protected void onMouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		ICompositeComponent.super.mouseReleased(mouseX, mouseY, button, state);
	}
	
	@Override
	protected void onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		ICompositeComponent.super.mouseDragged(mouseX, mouseY, button, dragX, dragY, state);
	}
	
	@Override
	protected void onMouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		ICompositeComponent.super.mouseScrolled(mouseX, mouseY, delta, state);
	}
	
	@Override
	protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		ICompositeComponent.super.keyPressed(keyCode, scanCode, modifiers, state);
	}
	
	@Override
	protected void onKeyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		ICompositeComponent.super.keyReleased(keyCode, scanCode, modifiers, state);
	}
	
	@Override
	protected void onCharTyped(char codePoint, int modifiers, EventState state)
	{
		ICompositeComponent.super.charTyped(codePoint, modifiers, state);
	}
	
	@Override
	protected void onCycleFocus(boolean forward, CycleFocusState state)
	{
		ICompositeComponent.super.cycleFocus(forward, state);
	}
	
}

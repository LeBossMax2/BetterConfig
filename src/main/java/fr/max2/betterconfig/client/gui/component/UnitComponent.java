package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.UnitLayoutConfig;

public abstract class UnitComponent extends Component<IComponent>
{
	public UnitComponent(String type)
	{
		super(type);
	}

	// Layout
	
	@Override
	protected UnitLayoutConfig getLayoutConfig()
	{
		return UnitLayoutConfig.INSTANCE;
	}
	
	@Override
	protected IComponent getLayoutParam()
	{
		return this;
	}

	@Override
	public void invalidate()
	{ }
	
	// Rendering

	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
	// Input handling

	@Override
	protected void onMouseMoved(double mouseX, double mouseY)
	{ }

	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{ }

	@Override
	protected void onMouseReleased(double mouseX, double mouseY, int button, EventState state)
	{ }

	@Override
	protected void onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{ }

	@Override
	protected void onMouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{ }

	@Override
	protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{ }

	@Override
	protected void onKeyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{ }

	@Override
	protected void onCharTyped(char codePoint, int modifiers, EventState state)
	{ }

	@Override
	protected void onCycleFocus(boolean forward, CycleFocusState state)
	{ }
	
}

package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.UnitLayoutConfig;

public abstract class UnitComponent extends Component<IComponent>
{
	public UnitComponent(IComponentParent layoutManager, String type)
	{
		super(layoutManager, type);
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
	public void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
	// Input handling

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{ }

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button, EventState state)
	{ }

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button, EventState state)
	{ }

	@Override
	public void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{ }

	@Override
	public void mouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{ }

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{ }

	@Override
	public void keyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{ }

	@Override
	public void charTyped(char codePoint, int modifiers, EventState state)
	{ }

	@Override
	public void cycleFocus(boolean forward, CycleFocusState state)
	{ }
	
}

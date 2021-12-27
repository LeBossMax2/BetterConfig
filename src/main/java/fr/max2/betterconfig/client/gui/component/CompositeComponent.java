package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;

public abstract class CompositeComponent extends Component<ICompositeComponent> implements ICompositeComponent
{
	protected IReadableList<IComponent> children;
	
	public CompositeComponent(String type, IReadableList<IComponent> children)
	{
		super(type);
		this.children = children;
		this.children.onChanged(new IListListener<IComponent>()
		{
			@Override
			public void onElementAdded(int index, IComponent newValue)
			{
				if (CompositeComponent.this.layoutManager != null)
					newValue.init(CompositeComponent.this.layoutManager, CompositeComponent.this);
			}

			@Override
			public void onElementRemoved(int index, IComponent oldValue)
			{
				oldValue.invalidate();
			}
		});
	}
	
	public CompositeComponent(String type)
	{
		this(type, new ObservableList<>());
	}

	public CompositeComponent(String type, List<? extends IComponent> initialChildren)
	{
		this(type, toReadable(initialChildren));
	}

	@SuppressWarnings("unchecked")
	private static IReadableList<IComponent> toReadable(List<? extends IComponent> list)
	{
		if (list instanceof IReadableList<?> rl)
		{
			return (IReadableList<IComponent>)rl;
		}
		else
		{
			return new ObservableList<>(list);
		}
	}
	
	@Override
	public List<? extends IComponent> getChildren()
	{
		return this.children;
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
	
	@Override
	public void init(IComponentParent layoutManager, IComponent parent)
	{
		super.init(layoutManager, parent);
		ICompositeComponent.super.init(layoutManager, parent);
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

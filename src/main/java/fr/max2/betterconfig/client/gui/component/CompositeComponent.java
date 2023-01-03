package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.util.IEvent;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class CompositeComponent extends BCComponent<ICompositeComponent> implements ICompositeComponent
{
	protected final IReadableList<IComponent> children;
	private final IEvent.Guard childrenGard;
	protected NarratableEntry lastNarratable;

	public CompositeComponent(String type, IReadableList<IComponent> children)
	{
		super(type);
		this.children = children;
		this.childrenGard = this.children.onChanged().add(new IListListener<IComponent>()
		{
			@Override
			public void onElementAdded(int index, IComponent newValue)
			{
				if (CompositeComponent.this.layoutManager != null)
				{
					newValue.init(CompositeComponent.this.layoutManager, CompositeComponent.this);
					CompositeComponent.this.layoutManager.marksLayoutDirty();
				}
			}

			@Override
			public void onElementRemoved(int index, IComponent oldValue)
			{
				oldValue.invalidate();
				if (CompositeComponent.this.layoutManager != null)
					CompositeComponent.this.layoutManager.marksLayoutDirty();
			}
		});
	}

	public CompositeComponent(String type)
	{
		this(type, new ObservableList<>());
	}

	public CompositeComponent(String type, List<IComponent> initialChildren)
	{
		this(type, toReadable(initialChildren));
	}

	@SuppressWarnings("unchecked")
	private static IReadableList<IComponent> toReadable(List<IComponent> list)
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

	@Override
	public void invalidate()
	{
		this.childrenGard.close();
	}

	// Rendering

	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		ICompositeComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state)
	{
		ICompositeComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks, state);
		super.onRenderOverlay(matrixStack, mouseX, mouseY, partialTicks, state);
	}

	// Input handling

	@Override
	protected void onMouseMoved(double mouseX, double mouseY)
	{
		ICompositeComponent.super.mouseMoved(mouseX, mouseY);
		this.updateFocus();
	}

	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		ICompositeComponent.super.mouseClicked(mouseX, mouseY, button, state);
		this.updateFocus();
	}

	@Override
	protected void onMouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		ICompositeComponent.super.mouseReleased(mouseX, mouseY, button, state);
		this.updateFocus();
	}

	@Override
	protected void onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		ICompositeComponent.super.mouseDragged(mouseX, mouseY, button, dragX, dragY, state);
		this.updateFocus();
	}

	@Override
	protected void onMouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		ICompositeComponent.super.mouseScrolled(mouseX, mouseY, delta, state);
		this.updateFocus();
	}

	@Override
	protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		ICompositeComponent.super.keyPressed(keyCode, scanCode, modifiers, state);
		this.updateFocus();
	}

	@Override
	protected void onKeyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		ICompositeComponent.super.keyReleased(keyCode, scanCode, modifiers, state);
		this.updateFocus();
	}

	@Override
	protected void onCharTyped(char codePoint, int modifiers, EventState state)
	{
		ICompositeComponent.super.charTyped(codePoint, modifiers, state);
		this.updateFocus();
	}

	@Override
	protected void onCycleFocus(boolean forward, CycleFocusState state)
	{
		ICompositeComponent.super.cycleFocus(forward, state);
		this.updateFocus();
	}

	protected void updateFocus()
	{
		this.hasFocus = this.getChildren().stream().anyMatch(IComponent::hasFocus);
	}

	// Narration

	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		narrationOutput = narrationOutput.nest();
		ImmutableList<NarratableEntry> narratableEntries = this.getChildren().stream().filter(NarratableEntry::isActive).collect(ImmutableList.toImmutableList());
		Screen.NarratableSearchResult res = Screen.findNarratableWidget(narratableEntries, this.lastNarratable);
		if (res != null)
		{
			if (res.priority.isTerminal())
				this.lastNarratable = res.entry;

			if (narratableEntries.size() > 1)
			{
				narrationOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", res.index + 1, narratableEntries.size()));

				if (res.priority == NarratableEntry.NarrationPriority.FOCUSED)
					narrationOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
			}

			res.entry.updateNarration(narrationOutput);
		}
		super.updateNarration(narrationOutput);
	}

}

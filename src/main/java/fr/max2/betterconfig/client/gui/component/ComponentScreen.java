package fr.max2.betterconfig.client.gui.component;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ComponentScreen extends Screen implements IComponentParent
{
	private final List<Runnable> delayedWork = new ArrayList<>();
	
	private final StyleSheet styleSheet;
	
	/** The current user interface */
	private IComponent content;

	protected boolean layoutDirty = false;

	protected ComponentScreen(Component title, StyleSheet styleSheet)
	{
		super(title);
		this.styleSheet = styleSheet;
	}
	
	// Style
	
	@Override
	public StyleSheet getStyleSheet()
	{
		return this.styleSheet;
	}
	
	// Layout
	
	protected void setContent(IComponent content)
	{
		if (this.content != null)
			this.content.invalidate();
		
		this.content = content;
		this.content.init(this, null);
		this.marksLayoutDirty();
	}
	
	public void checkLayout()
	{
		if (this.layoutDirty)
		{
			this.layoutDirty = false;
			this.updateLayout(this.content, new Size(this.width, this.height));
		}
	}

	@Override
	public void marksLayoutDirty()
	{
		this.layoutDirty = true;
	}

	@Override
	public int getLayoutX()
	{
		return 0;
	}

	@Override
	public int getLayoutY()
	{
		return 0;
	}
	
	@Override
	public void removed()
	{
		if (this.content != null)
			this.content.invalidate();
		
		super.removed();
	}
	
	@Override
	public void enqueueWork(Runnable action)
	{
		this.delayedWork.add(action);
	}
	
	private void processDelayedWork()
	{
		for (Runnable action : this.delayedWork)
		{
			action.run();
		}
		this.delayedWork.clear();
	}
	
	// Rendering

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.checkLayout();
		
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.content.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		
		this.processDelayedWork();
	}
	
	// Mouse handling
	
	@Override
	public void mouseMoved(double pMouseX, double pMouseY)
	{
		this.checkLayout();
		
		this.content.mouseMoved(pMouseX, pMouseY);
		
		super.mouseMoved(pMouseX, pMouseY);
		
		this.processDelayedWork();
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.mouseClicked(pMouseX, pMouseY, pButton, state);
		
		boolean res = state.isConsumed() || super.mouseClicked(pMouseX, pMouseY, pButton);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.mouseReleased(pMouseX, pMouseY, pButton, state);
		boolean res = state.isConsumed() || super.mouseReleased(pMouseX, pMouseY, pButton);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY, state);
		boolean res = state.isConsumed() || super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.mouseScrolled(pMouseX, pMouseY, pDelta, state);
		boolean res = state.isConsumed() || super.mouseScrolled(pMouseX, pMouseY, pDelta);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.keyPressed(pKeyCode, pScanCode, pModifiers, state);
		boolean res = state.isConsumed() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.keyReleased(pKeyCode, pScanCode, pModifiers, state);
		boolean res = state.isConsumed() || super.keyReleased(pKeyCode, pScanCode, pModifiers);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean charTyped(char pCodePoint, int pModifiers)
	{
		this.checkLayout();
		
		EventState state = new EventState();
		this.content.charTyped(pCodePoint, pModifiers, state);
		boolean res = state.isConsumed() || super.charTyped(pCodePoint, pModifiers);
		
		this.processDelayedWork();
		
		return res;
	}
	
	@Override
	public boolean changeFocus(boolean forward)
	{
		this.checkLayout();
		
		boolean res = forward ? cycleFocusForward() : cycleFocusBackward();
		
		this.processDelayedWork();
		
		return res;
	}

	private boolean cycleFocusForward()
	{
		// Forward traversal : pass through Component, then GuiEventListeners
		boolean forward = true;
		
		if (this.getFocused() != null)
		{
			// Some event handler is focused
			CycleFocusState state = new CycleFocusState();
			state.consume();
			this.content.cycleFocus(forward, state);
			
			return super.changeFocus(forward);
		}

		CycleFocusState state = new CycleFocusState();
		this.content.cycleFocus(forward, state);
		
		if (state.isConsumed())
			return true;
		
		if (state.isPropagated())
			return super.changeFocus(forward);
		
		// Nothing is focused, make a normal pass
		state.propagate();
		this.content.cycleFocus(forward, state);
		
		if (state.isConsumed())
			return true;

		return super.changeFocus(forward);
	}

	private boolean cycleFocusBackward()
	{
		// Backward traversal : pass through GuiEventListeners, then Component
		boolean forward = false;
		
		if (this.getFocused() != null)
		{
			// Some event handler is focused
			boolean res = super.changeFocus(forward);
			
			CycleFocusState state = new CycleFocusState();
			state.consume();
			this.content.cycleFocus(forward, state);
			
			return res;
		}
		//
		CycleFocusState state = new CycleFocusState();
		this.content.cycleFocus(forward, state);
		
		if (state.isConsumed())
			return true;
		
		if (state.isPropagated())
			return false;

		// Nothing is focused, make a normal pass
		if (super.changeFocus(forward))
			return true;
		
		state.propagate();
		this.content.cycleFocus(forward, state);
		
		return state.isConsumed();
	}
	
}

package fr.max2.betterconfig.client.gui.better;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.component.ScrollPane;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.Minecraft;

public class BetterScrollPane extends ScrollPane implements IBetterElement
{
	/** Indicates whether the layout is dirty */
	private boolean dirty = true;
	/** The filter from the search bar */
	private ConfigFilter filter = ConfigFilter.ALL;

	public BetterScrollPane(Minecraft minecraft, int x, int y, int w, int h, IBetterElement content)
	{
		super(minecraft, x, y, w, h, content);
	}
	
	// Layout

	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		this.baseY = y;
		this.filter = filter;
		this.checkLayout();
		
		return this.getHeight();
	}
	
	protected void checkLayout()
	{
		if (this.isDirty())
		{
			((IBetterElement)this.content).setYgetHeight(0, this.filter);
			this.clean();
		}
	}
	
	@Override
	public void marksLayoutDirty()
	{
		this.dirty = true;
	}
	
	/** Marks the layout as not dirty */
	private void clean()
	{
		this.dirty = false;
	}
	
	/** Indicates whether the layout is dirty */
	private boolean isDirty()
	{
		return this.dirty;
	}
	
	// User interaction
	
	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		super.mouseMoved(mouseX, mouseY);
		this.checkLayout();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean res = super.mouseClicked(mouseX, mouseY, button);
		this.checkLayout();
		return res;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		boolean res = super.mouseReleased(mouseX, mouseY, button);
		this.checkLayout();
		return res;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		boolean res = super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
		this.checkLayout();
		return res;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta)
	{
		boolean res = super.mouseScrolled(mouseX, mouseY, delta);
		this.checkLayout();
		return res;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		boolean res = super.keyPressed(keyCode, scanCode, modifiers);
		this.checkLayout();
		return res;
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers)
	{
		boolean res = super.keyReleased(keyCode, scanCode, modifiers);
		this.checkLayout();
		return res;
	}
	
	@Override
	public boolean charTyped(char codePoint, int modifiers)
	{
		boolean res = super.charTyped(codePoint, modifiers);
		this.checkLayout();
		return res;
	}
	
	@Override
	public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.isMouseOver(mouseX, mouseY))
			super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
	}
}

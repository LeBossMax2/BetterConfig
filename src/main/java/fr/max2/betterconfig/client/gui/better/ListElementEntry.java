package fr.max2.betterconfig.client.gui.better;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

public class ListElementEntry extends FocusableGui implements INestedGuiComponent, IBetterElement
{
	private final BetterConfigScreen screen;
	private final IBetterElement content;
	private final IBetterElement button;
	private final List<IBetterElement> children;
	private final int width;
	private int height = 0;
	private final int baseX;
	private int baseY;
	private boolean hidden;
	private ILayoutManager layout;
	
	public ListElementEntry(BetterConfigScreen screen, IBetterElement content, int x, int width, IPressable deleteAction)
	{
		this.screen = screen;
		this.content = content;
		this.button = new BetterButton.Icon(screen, x, 0, 0, new StringTextComponent("X"), deleteAction, new TranslationTextComponent(REMOVE_TOOLTIP_KEY));
		this.children = Arrays.asList(content, this.button);
		this.baseX = x;
		this.width = width;
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		INestedGuiComponent.super.setLayoutManager(manager);
	}

	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		this.baseY = y;
		this.height = this.content.setYgetHeight(y, filter);
		this.hidden = this.height == 0;
		this.button.setYgetHeight(y + (VALUE_CONTAINER_HEIGHT - VALUE_HEIGHT) / 2, this.hidden ? ConfigFilter.NONE : filter);
		return this.height;
	}

	@Override
	public List<? extends IGuiComponent> getEventListeners()
	{
		return this.children;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		if (this.hidden)
			return false;
		
		int y = this.baseY  + this.layout.getLayoutY();
		return mouseX >= this.baseX + this.layout.getLayoutX()
		    && mouseY >= y
		    && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING
		    && mouseY < y + this.height;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.hidden)
			return;
		
		if (this.isMouseOver(mouseX, mouseY))
		{
			this.button.render(matrixStack, mouseX, mouseY, partialTicks);
		}
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}

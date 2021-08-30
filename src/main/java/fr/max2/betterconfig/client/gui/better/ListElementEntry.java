package fr.max2.betterconfig.client.gui.better;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.Button.OnPress;
import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

public class ListElementEntry extends CompositeComponent implements IBetterElement
{
	private final IBetterElement content;
	private final BetterButton button;
	private final List<IComponent> children;
	private boolean hidden;

	private final CompositeLayoutConfig config = new CompositeLayoutConfig();
	
	public ListElementEntry(BetterConfigScreen screen, IComponentParent layoutManager, IBetterElement content, OnPress deleteAction)
	{
		super(screen);
		this.content = content;
		this.button = new BetterButton.Icon(screen, layoutManager, 0, 0, new TextComponent("X"), deleteAction, new TranslatableComponent(REMOVE_TOOLTIP_KEY));
		this.children = Arrays.asList(this.button, content);
		this.button.config.outerPadding = new Padding((VALUE_CONTAINER_HEIGHT - VALUE_HEIGHT) / 2, 0, 0, 0);
		//this.config.innerPadding = new Padding(0, RIGHT_PADDING, 0, X_PADDING);?
		this.config.dir = Axis.HORIZONTAL;
	}
	
	@Override
	protected CompositeLayoutConfig getLayoutConfig()
	{
		return this.config;
	}
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		this.hidden = this.content.filterElements(filter);
		return this.hidden;
	}

	@Override
	public List<? extends IComponent> getChildren()
	{
		return this.children;
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.hidden)
			return;
		
		if (this.isPointInside(mouseX, mouseY))
		{
			this.button.render(matrixStack, mouseX, mouseY, partialTicks);
		}
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}

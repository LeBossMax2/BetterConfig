package fr.max2.betterconfig.client.gui.better;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.widget.Button.OnPress;
import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

public class ListElementEntry extends CompositeComponent implements IBetterElement
{
	public static final StyleRule STYLE = StyleRule.when().type("better:list_entry").then()
			.set(CompositeLayoutConfig.DIR, Axis.HORIZONTAL)
			.set(ComponentLayoutConfig.OUTER_PADDING, new Padding(0, 0, 0, -VALUE_HEIGHT))
			.build();
	public static final StyleRule REMOVE_STYLE = StyleRule.when().hasClass("better:list_remove").then()
			.set(ComponentLayoutConfig.OUTER_PADDING, new Padding((VALUE_CONTAINER_HEIGHT - VALUE_HEIGHT) / 2, 0, 0, 0))
			.build();
	
	private final IBetterElement content;
	private final BetterButton button;
	private boolean filteredOut = false;
	
	public ListElementEntry(BetterConfigScreen screen, IBetterElement content, OnPress deleteAction)
	{
		super("better:list_entry");
		this.content = content;
		this.button = new BetterButton.Icon(screen, 0, 0, new TextComponent("X"), deleteAction, new TranslatableComponent(REMOVE_TOOLTIP_KEY));
		this.button.addClass("better:list_remove");
		this.children.addAll(Arrays.asList(this.button, content));
		this.registerProperty(FILTERED_OUT, () -> this.filteredOut);
		//this.config.innerPadding = new Padding(0, RIGHT_PADDING, 0, X_PADDING);?
	}
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		this.filteredOut = this.content.filterElements(filter);
		return this.filteredOut;
	}
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
		if (this.isPointInside(mouseX, mouseY))
			this.button.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}

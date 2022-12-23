package fr.max2.betterconfig.client.gui.better;

import java.util.Arrays;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.widget.Button.OnPress;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.network.chat.Component;

public class ListElementEntry extends CompositeComponent implements IBetterElement
{
	private final IBetterElement content;
	private boolean filteredOut = false;

	public ListElementEntry(BetterConfigScreen screen, IBetterElement content, OnPress deleteAction)
	{
		super("better:list_entry");
		this.content = content;
		BetterButton button = new BetterButton.Icon(screen, 0, 0, Component.literal("X"), Component.translatable(GuiTexts.REMOVE_TOOLTIP_KEY));
		button.addOnPressed(deleteAction);
		button.addClass("better:list_remove");
		this.children.addAll(Arrays.asList(button, content));
		this.registerProperty(FILTERED_OUT, () -> this.filteredOut);
	}

	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		this.filteredOut = this.content.filterElements(filter);
		return this.filteredOut;
	}
}

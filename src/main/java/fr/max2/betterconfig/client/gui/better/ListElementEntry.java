package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.widget.Button.OnPress;
import fr.max2.betterconfig.client.util.GuiTexts;
import net.minecraft.network.chat.Component;

public class ListElementEntry extends CompositeComponent implements IBetterElement
{
	private final IBetterElement content;
	private boolean filteredOut = false;

	public ListElementEntry(BetterConfigScreen screen, IBetterElement content, OnPress deleteAction)
	{
		super("better:list_entry");
		this.content = content;
		BetterButton deleteButton = new BetterButton.Icon(screen, 0, 0, Component.translatable(GuiTexts.REMOVE_ELEMENT_KEY), Component.translatable(GuiTexts.REMOVE_TOOLTIP_KEY));
		deleteButton.addOnPressed(deleteAction);
		deleteButton.addClass("better:list_remove");
		this.children.addAll(List.of(deleteButton, content));
		this.registerProperty(FILTERED_OUT, () -> this.filteredOut);
	}

	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		this.filteredOut = this.content.filterElements(filter);
		return this.filteredOut;
	}
}

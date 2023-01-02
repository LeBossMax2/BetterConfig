package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.value.ConfigList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ListGroup extends GuiGroup
{
	public static final PropertyIdentifier<Boolean> EMPTY = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "empty"));

	private ListGroup(List<IComponent> contentList, ConfigList list)
	{
		super(contentList);
		this.addClass("better:list_group");
		this.registerProperty(EMPTY, () -> list.getValueList().isEmpty());
	}

	public ListGroup(BetterConfigScreen screen, IComponent content, ConfigList list)
	{
		this(makeContentList(screen, content, list), list);
	}

	private static List<IComponent> makeContentList(BetterConfigScreen screen, IComponent actualContent, ConfigList list)
	{
		var addFirstButton = new BetterButton(screen, Component.translatable(GuiTexts.ADD_ELEMENT_KEY), Component.translatable(GuiTexts.ADD_FIRST_TOOLTIP_KEY))
			.addOnPressed(() -> list.addValue(0))
			.addClass("better:list_add_first");
		
		var addLastButton = new BetterButton(screen, Component.translatable(GuiTexts.ADD_ELEMENT_KEY), Component.translatable(GuiTexts.ADD_LAST_TOOLTIP_KEY))
			.addOnPressed(() -> list.addValue(list.getValueList().size()))
			.addClass("better:list_add_last");

		return List.of(addFirstButton, actualContent, addLastButton);
	}
}

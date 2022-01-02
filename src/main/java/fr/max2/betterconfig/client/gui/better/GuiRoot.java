package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.HBox;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.widget.Text;
import fr.max2.betterconfig.client.gui.component.widget.TextField;
import fr.max2.betterconfig.client.gui.layout.Alignment;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.config.ModConfig;

/** The container for the main section */
public class GuiRoot extends CompositeComponent
{
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The scroll panel */
	private final BetterScrollPane scrollPane;
	/** The filter from the search bar */
	private final ConfigFilter filter = new ConfigFilter();

	public GuiRoot(BetterConfigScreen screen, IComponent content)
	{
		super("better:root");
		this.screen = screen;
		
		// Tabs
		int i = 0;
		List<IComponent> tabs = new ArrayList<>();
		for (ModConfig config : screen.getModConfigs())
		{
			final int index = i;
			// TODO [#2] Add meaningful tooltip : explanation of config types + file path
			BetterButton b = new BetterButton(screen, new TextComponent(config.getType().name()), thisButton -> this.screen.openConfig(index), new TextComponent(config.getFileName()));
			b.addClass("better:tab_button");
			b.widget.active = index != screen.getCurrentConfigIndex();
			i++;
			tabs.add(b);
		}
		HBox tabHolder = new HBox(tabs);
		tabHolder.addClass("better:tab_bar");
		this.children.add(tabHolder);
		
		// Search bar
		Component searchText = new TranslatableComponent(GuiTexts.SEARCH_BAR_KEY);
		Text searchLabel = new Text(searchText, Alignment.CENTER, Alignment.MIN);
		searchLabel.addClass("better:search_label");
		TextField searchField = new TextField(screen.getFont(), searchText);
		searchField.addClass("better:search_field");
		searchField.setResponder(this::updateFilter);
		HBox searchBar = new HBox(Arrays.asList(searchLabel, searchField));
		searchBar.addClass("better:search_bar");
		this.children.add(searchBar);
		
		// Scroll
		this.scrollPane = new BetterScrollPane(content);
		this.scrollPane.filterElements(this.filter);
		this.children.add(this.scrollPane);
		
		// Cancel/Save buttons
		BetterButton cancelButton = new BetterButton(new TranslatableComponent(GuiTexts.CANCEL_CONFIG_KEY), thisButton -> this.screen.cancelChanges());
		cancelButton.addClass("better:cancel");
		BetterButton saveButton = new BetterButton(new TranslatableComponent(GuiTexts.SAVE_CONFIG_KEY), thisButton -> this.screen.onClose());
		saveButton.addClass("better:save");
		HBox buttonBar = new HBox(Arrays.asList(cancelButton, saveButton));
		buttonBar.addClass("better:bottom_bar");
		this.children.add(buttonBar);
		
		this.setStyle(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(this.screen.width, this.screen.height));
	}
	
	// Layout
	
	/** Updates the content using the given filter string */
	private void updateFilter(String filterStr)
	{
		this.filter.setFilter(filterStr);
		this.scrollPane.filterElements(this.filter);
		this.scrollPane.marksLayoutDirty();
	}
	
	// Rendering
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.screen.renderBackground(matrixStack);
		super.onRender(matrixStack, mouseX, mouseY, partialTicks);
	}
	
}

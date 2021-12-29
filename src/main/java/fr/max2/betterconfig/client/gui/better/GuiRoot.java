package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.HBox;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.client.gui.component.widget.TextField;
import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.config.ModConfig;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The container for the main section */
public class GuiRoot extends CompositeComponent
{
	/** The x position of the input field of the search bar */
	private static final int SEARCH_LABEL_WIDTH = 80;
	
	public static final StyleRule ROOT_STYLE = StyleRule.when().type("better:root").then()
			.set(CompositeLayoutConfig.DIR, Axis.VERTICAL)
			.set(CompositeLayoutConfig.SPACING, Y_PADDING)
			.set(CompositeLayoutConfig.INNER_PADDING, new Padding(Y_PADDING, X_PADDING, Y_PADDING, X_PADDING))
			.build();
	
	public static final StyleRule SEARCH_STYLE = StyleRule.when().hasClass("better:search_field").then()
			.set(ComponentLayoutConfig.OUTER_PADDING, new Padding(1, 1, 1, SEARCH_LABEL_WIDTH + 1))
			.set(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, 18))
			.build();
	
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The text field of the search bar */
	private final TextField searchField;
	/** The scroll panel */
	private final BetterScrollPane scrollPane;
	/** The filter from the search bar */
	private final ConfigFilter filter = new ConfigFilter();

	public GuiRoot(BetterConfigScreen screen, IComponent content)
	{
		super("better:root");
		this.screen = screen;
		
		// Tabs
		int tabButtonWidth = (this.screen.width - 2 * X_PADDING) / ModConfig.Type.values().length;
		int i = 0;
		List<IComponent> tabs = new ArrayList<>();
		for (ModConfig config : screen.getModConfigs())
		{
			final int index = i;
			// TODO [#2] add meaningful tooltip
			Button b = new Button(new TextComponent(config.getFileName()), thisButton -> this.screen.openConfig(index), Button.NO_OVERLAY);
			b.addClass("better:tab_button");
			b.setStyle(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(tabButtonWidth, 20));
			b.widget.active = index != screen.getCurrentConfigIndex();
			i++;
			tabs.add(b);
		}
		HBox tabHolder = new HBox(tabs);
		tabHolder.addClass("better:tab_bar");
		this.children.add(tabHolder);
		
		// Search bar
		this.searchField = new TextField(screen.getFont(), new TranslatableComponent(SEARCH_BAR_KEY));
		this.searchField.addClass("better:search_field");
		this.searchField.setResponder(this::updateFilter);
		this.children.add(this.searchField);
		
		// Scroll
		this.scrollPane = new BetterScrollPane(content);
		this.scrollPane.filterElements(this.filter);
		this.children.add(this.scrollPane);
		
		// Cancel/Save buttons
		List<IComponent> buttons = new ArrayList<>();
		int buttonWidth = (this.screen.width - 2 * X_PADDING) / 2;
		Button cancelButton = new Button(new TranslatableComponent(CANCEL_CONFIG_KEY), thisButton -> this.screen.cancelChanges(), Button.NO_OVERLAY);
		cancelButton.addClass("better:cancel");
		cancelButton.setStyle(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(buttonWidth, 20));
		buttons.add(cancelButton);
		Button saveButton = new Button(new TranslatableComponent(SAVE_CONFIG_KEY), thisButton -> this.screen.onClose(), Button.NO_OVERLAY);
		saveButton.addClass("better:save");
		saveButton.setStyle(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(buttonWidth, 20));
		buttons.add(saveButton);
		HBox buttonBar = new HBox(buttons);
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
		Font font = this.screen.getFont();
		font.draw(matrixStack, this.searchField.getMessage(), X_PADDING, 20 + 2 * Y_PADDING + (20 - font.lineHeight) / 2, 0xFF_FF_FF_FF);
		this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	protected void renderHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
}

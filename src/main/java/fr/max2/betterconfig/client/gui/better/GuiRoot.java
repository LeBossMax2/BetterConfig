package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.HBox;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.client.gui.component.widget.TextField;
import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Size;
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
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The text field of the search bar */
	private final TextField searchField;
	/** The scroll panel */
	private final BetterScrollPane scrollPane;
	/** The tab buttons */
	private final List<IComponent> components = new ArrayList<>();
	/** The filter from the search bar */
	private final ConfigFilter filter = new ConfigFilter();

	private final CompositeLayoutConfig config = new CompositeLayoutConfig();

	public GuiRoot(BetterConfigScreen screen, Function<IComponentParent, IComponent> content)
	{
		super(screen);
		this.screen = screen;
		
		// Tabs
		int tabButtonWidth = (this.screen.width - 2 * X_PADDING) / ModConfig.Type.values().length;
		int i = 0;
		List<IComponent> tabs = new ArrayList<>();
		for (ModConfig config : screen.getModConfigs())
		{
			final int index = i;
			Button b = new Button(screen, new TextComponent(config.getFileName()), thisButton -> this.screen.openConfig(index), Button.NO_TOOLTIP);
			b.config.sizeOverride = new Size(tabButtonWidth, 20);
			b.widget.active = index != screen.getCurrentConfigIndex();
			i++;
			tabs.add(b);
		}
		HBox tabHolder = new HBox(screen, tabs);
		this.components.add(tabHolder);
		
		// Search bar
		this.searchField = new TextField(screen, screen.getFont(), new TranslatableComponent(SEARCH_BAR_KEY));
		this.searchField.setResponder(this::updateFilter);
		this.searchField.config.outerPadding = new Padding(1, 1, 1, SEARCH_LABEL_WIDTH + 1);
		this.searchField.config.sizeOverride.height = 18;
		this.components.add(this.searchField);
		
		// Scroll
		this.scrollPane = new BetterScrollPane(screen, screen.getMinecraft(), content);
		this.scrollPane.filterElements(this.filter);
		this.components.add(this.scrollPane);
		
		// Cancel/Save buttons
		List<IComponent> buttons = new ArrayList<>();
		int buttonWidth = (this.screen.width - 2 * X_PADDING) / 2;
		Button cancelButton = new Button(screen, new TranslatableComponent(CANCEL_CONFIG_KEY), thisButton -> this.screen.cancelChanges(), Button.NO_TOOLTIP);
		cancelButton.config.sizeOverride = new Size(buttonWidth, 20);
		buttons.add(cancelButton);
		Button saveButton = new Button(screen, new TranslatableComponent(SAVE_CONFIG_KEY), thisButton -> this.screen.onClose(), Button.NO_TOOLTIP);
		saveButton.config.sizeOverride = new Size(buttonWidth, 20);
		buttons.add(saveButton);
		HBox buttonBar = new HBox(screen, buttons);
		this.components.add(buttonBar);
		
		this.config.dir = Axis.VERTICAL;
		this.config.spacing = Y_PADDING;
		this.config.sizeOverride = new Size(this.screen.width, this.screen.height);
		this.config.innerPadding = new Padding(Y_PADDING, X_PADDING, Y_PADDING, X_PADDING);
	}
	
	// Layout
	
	@Override
	protected CompositeLayoutConfig getLayoutConfig()
	{
		return this.config;
	}

	@Override
	public List<? extends IComponent> getChildren()
	{
		return this.components;
	}
	
	/** Updates the content using the given filter string */
	private void updateFilter(String filterStr)
	{
		this.filter.setFilter(filterStr);
		this.scrollPane.marksLayoutDirty();
	}
	
	// Rendering
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.screen.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		Font font = this.screen.getFont();
		font.draw(matrixStack, this.searchField.getMessage(), X_PADDING, 20 + 2 * Y_PADDING + (20 - font.lineHeight) / 2, 0xFF_FF_FF_FF);
		this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	protected void renderHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
}

package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.Button;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.client.gui.component.TextField;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.config.ModConfig;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The container for the main section */
public class GuiRoot extends AbstractContainerEventHandler implements INestedGuiComponent
{
	/** The the height of the header */
	private static final int CONTAINER_HEADER_HEIGHT = 60;
	/** The the height of the header */
	private static final int CONTAINER_FOOTER_HEIGHT = 30;
	/** The x position of the input field of the search bar */
	private static final int SEARCH_LABEL_WIDTH = 80;
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The text field of the search bar */
	private final TextField searchField;
	/** The scroll panel */
	private final BetterScrollPane scrollPane;
	/** The tab buttons */
	private final List<IGuiComponent> components = new ArrayList<>();
	/** The filter from the search bar */
	private final ConfigFilter filter = new ConfigFilter();

	public GuiRoot(BetterConfigScreen screen, IBetterElement content)
	{
		this.screen = screen;
		int x = X_PADDING;
		
		// Tabs
		int tabButtonWidth = (this.screen.width - 2 * X_PADDING) / ModConfig.Type.values().length;
		int i = 0;
		for (ModConfig config : screen.getModConfigs())
		{
			final int index = i;
			Button b = new Button(x, Y_PADDING, tabButtonWidth, 20, new TextComponent(config.getFileName()), thisButton -> this.screen.openConfig(index), Button.NO_TOOLTIP);
			b.active = index != screen.getCurrentConfigIndex();
			this.components.add(b);
			
			x += tabButtonWidth;
			i++;
		}
		
		// Search bar
		this.searchField = new TextField(screen.getFont(), X_PADDING + SEARCH_LABEL_WIDTH + 1, 20 + 2 * Y_PADDING + 1, this.screen.width - 2 * X_PADDING - SEARCH_LABEL_WIDTH - 2, 20 - 2, new TranslatableComponent(SEARCH_BAR_KEY));
		this.searchField.setResponder(this::updateFilter);
		this.components.add(this.searchField);
		
		// Scroll
		this.scrollPane = new BetterScrollPane(screen.getMinecraft(), X_PADDING, Y_PADDING + CONTAINER_HEADER_HEIGHT, screen.width - 2 * X_PADDING, screen.height - 2 * Y_PADDING - CONTAINER_HEADER_HEIGHT - CONTAINER_FOOTER_HEIGHT, content);
		this.components.add(this.scrollPane);
		this.scrollPane.setYgetHeight(Y_PADDING + CONTAINER_HEADER_HEIGHT, this.filter);
		
		// Cancel/Save buttons
		int buttonWidth = (this.screen.width - 2 * X_PADDING) / 2;
		Button cancelButton = new Button(X_PADDING, screen.height - Y_PADDING - 20, buttonWidth, 20, new TranslatableComponent(CANCEL_CONFIG_KEY), thisButton -> this.screen.cancelChanges(), Button.NO_TOOLTIP);
		this.components.add(cancelButton);
		Button saveButton = new Button(X_PADDING + buttonWidth, screen.height - Y_PADDING - 20, buttonWidth, 20, new TranslatableComponent(SAVE_CONFIG_KEY), thisButton -> this.screen.onClose(), Button.NO_TOOLTIP);
		this.components.add(saveButton);
	}
	
	// Layout

	@Override
	public List<? extends IGuiComponent> children()
	{
		return this.components;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return true;
	}
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{ }

	@Override
	public int getWidth()
	{
		return this.screen.width;
	}

	@Override
	public int getHeight()
	{
		return this.screen.height;
	}
	
	/** Updates the content using the given filter string */
	private void updateFilter(String filterStr)
	{
		this.filter.setFilter(filterStr);
		this.scrollPane.marksLayoutDirty();
		this.scrollPane.checkLayout();
	}
	
	// Rendering
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.screen.renderBackground(matrixStack);
		INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
		Font font = this.screen.getFont();
		font.draw(matrixStack, this.searchField.getMessage(), X_PADDING, 20 + 2 * Y_PADDING + (20 - font.lineHeight) / 2, 0xFF_FF_FF_FF);
		this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	protected void renderHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
}

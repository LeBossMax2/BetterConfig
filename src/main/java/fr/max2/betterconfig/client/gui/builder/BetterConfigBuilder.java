package fr.max2.betterconfig.client.gui.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.Button;
import fr.max2.betterconfig.client.gui.component.CycleOptionButton;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.client.gui.component.NumberField;
import fr.max2.betterconfig.client.gui.component.ScrollPane;
import fr.max2.betterconfig.client.gui.component.TextField;
import fr.max2.betterconfig.client.util.INumberType;
import fr.max2.betterconfig.client.util.NumberTypes;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.spec.ConfigLocation;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigValueVisitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.config.ModConfig;

/** A builder for better configuration screen */
public class BetterConfigBuilder
{
	/** The translation key for displaying the default value */
	public static final String DEFAULT_VALUE_KEY = BetterConfig.MODID + ".option.default_value";
	/** The translation key for the text field of the search bar */
	public static final String SEARCH_BAR_KEY = BetterConfig.MODID + ".option.search";
	/** The translation key for the text on the add element button in lists */
	public static final String ADD_ELEMENT_KEY = BetterConfig.MODID + ".list.add";
	/** The translation key for the tooltip on the add element button at the start of lists */
	public static final String ADD_FIRST_TOOLTIP_KEY = BetterConfig.MODID + ".list.add.first.tooltip";
	/** The translation key for the tooltip on the add element button at the end of lists */
	public static final String ADD_LAST_TOOLTIP_KEY = BetterConfig.MODID + ".list.add.last.tooltip";
	/** The translation key for the tooltop of the button to remove elements from the list */
	public static final String REMOVE_TOOLTIP_KEY = BetterConfig.MODID + ".list.remove.tooltip";
	
	public static final ResourceLocation BETTER_ICONS = new ResourceLocation(BetterConfig.MODID, "textures/gui/better_icons.png");
	
	/** The width of the indentation added for each nested section */
	private static final int SECTION_TAB_SIZE = 22;
	/** The left and right padding around the screen */
	private static final int X_PADDING = 10;
	/** The top and bottom padding around the screen */
	private static final int Y_PADDING = 10;
	/** The right padding around the screen */
	private static final int RIGHT_PADDING = 10;
	/** The height of the value entries */
	private static final int VALUE_CONTAINER_HEIGHT = 24;
	/** The height of the value widget */
	private static final int VALUE_HEIGHT = 20;
	/** The width of the value widget */
	private static final int VALUE_WIDTH = 150;
	
	/** The default color of the text in a text field */
	private static final int DEFAULT_FIELD_TEXT_COLOR = 0xFF_E0_E0_E0;
	/** The color of the text in a text field when the value is not valid */
	private static final int ERROR_FIELD_TEXT_COLOR   = 0xFF_FF_00_00;
	
	/**
	 * Builds the user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static IGuiComponent build(BetterConfigScreen screen, IConfigTable config)
	{
		return new UIContainer(screen, Builder.buildTable(screen, config, 0));
	}
	
	/** The visitor to build the gui components */
	private static class Builder implements IConfigValueVisitor<ConfigTableEntrySpec, IBetterElement>, IConfigPrimitiveVisitor<ConfigTableEntrySpec, IBetterElement>
	{
		/** The position of the value widgets relative to the right side */
		private static final int VALUE_OFFSET = 2 * X_PADDING + RIGHT_PADDING + VALUE_WIDTH + 4;
		
		/** The parent screen */
		private final BetterConfigScreen screen;
		private final Integer xOffset;

		private Builder(BetterConfigScreen screen, Integer xOffset)
		{
			this.screen = screen;
			this.xOffset = xOffset;
		}
		
		private static IBetterElement buildTable(BetterConfigScreen screen, IConfigTable table, int xOffset)
		{
			List<IBetterElement> content = table.exploreEntries((e, value) -> value.exploreNode(new Builder(screen, xOffset), e)).collect(Collectors.toList());
			return new UIGroup(screen.width - 2 * X_PADDING - RIGHT_PADDING - xOffset, content);
		}
		
		private void buildList(IConfigList list, int xOffset, ConfigTableEntrySpec entry, List<IBetterElement> content, List<ListElemInfo> entries, Runnable layoutUpdater)
		{
			int i = 0;

			List<? extends IConfigNode<?>> values = list.getValueList();
			content.add(new BetterButton(this.screen, xOffset, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH, new TranslationTextComponent(ADD_ELEMENT_KEY), thiz ->
			{
				IConfigNode<?> elem = list.addValue(0);
				putListElement(list, elem, xOffset, entry, content, entries, layoutUpdater, 0);
				layoutUpdater.run();
			}, new TranslationTextComponent(ADD_FIRST_TOOLTIP_KEY)));
			
			for (IConfigNode<?> elem : values)
			{
				putListElement(list, elem, xOffset, entry, content, entries, layoutUpdater, i);
				i++;
			}
			
			layoutUpdater.run();
		}
		
		private void putListElement(IConfigList list, IConfigNode<?> elem, int xOffset, ConfigTableEntrySpec entry, List<IBetterElement> content, List<ListElemInfo> entries, Runnable layoutUpdater, int index)
		{
			ConfigTableEntrySpec elemEntry = new ConfigTableEntrySpec(new ConfigLocation(entry.getLoc(), entry.getLoc().getName() + "[" + index + "]"), elem.getSpec(), new StringTextComponent("[" + index + "]"), entry.getCommentString());
			ListElemInfo info = new ListElemInfo(index, entry, elemEntry);
			boolean isFirst = content.size() <= 1;
			entries.add(index, info);
			IBetterElement child = elem.exploreNode(new Builder(this.screen, xOffset), elemEntry);
			content.add(index + 1, new ListElementEntry(this.screen, child, xOffset - SECTION_TAB_SIZE, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH + SECTION_TAB_SIZE, deleteButton ->
			{
				int myIndex = info.getIndex();
				list.removeValueAt(myIndex);
				content.remove(myIndex + 1);
				entries.remove(myIndex);
				for (int i = myIndex; i < entries.size(); i++)
				{
					entries.get(i).updateIndex(i);
				}
				layoutUpdater.run();
				if (content.size() == 2)
				{
					// Remove "add last" button
					content.remove(1);
				}
			}));
			for (int i = index + 1; i < entries.size(); i++)
			{
				entries.get(i).updateIndex(i);
			}
			
			if (isFirst)
			{
				// Add "add last" button
				content.add(new BetterButton(this.screen, xOffset, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH, new TranslationTextComponent(ADD_ELEMENT_KEY), thiz ->
				{
					int lastIndex = list.getValueList().size();
					IConfigNode<?> newElem = list.addValue(lastIndex);
					putListElement(list, newElem, xOffset, entry, content, entries, layoutUpdater, lastIndex);
					layoutUpdater.run();
				}, new TranslationTextComponent(ADD_LAST_TOOLTIP_KEY)));
			}
		}
		
		// Table entry visitor
		
		@Override
		public IBetterElement visitTable(IConfigTable table, ConfigTableEntrySpec entry)
		{
			return new UIFoldout(this.screen, entry, buildTable(this.screen, table, this.xOffset + SECTION_TAB_SIZE), this.xOffset);
		}
		
		@Override
		public IBetterElement visitList(IConfigList list, ConfigTableEntrySpec entry)
		{
			int offset = this.xOffset + SECTION_TAB_SIZE;
			List<IBetterElement> content = new ArrayList<>();
			UIGroup uiGroup = new UIGroup(this.screen.width - 2 * X_PADDING - RIGHT_PADDING - offset, content);
			this.buildList(list, offset, entry, content, new ArrayList<>(), uiGroup::updateLayout);
			return new UIFoldout(this.screen, entry, uiGroup, this.xOffset);
		}
		
		@Override
		public <T> IBetterElement visitProperty(IConfigPrimitive<T> property, ConfigTableEntrySpec entry)
		{
			IBetterElement widget = property.exploreType(this, entry);
			return new ValueContainer(this.screen, entry, property, widget, this.xOffset);
		}
		
		// Property visitor
		
		@Override
		public IBetterElement visitBoolean(IConfigPrimitive<Boolean> property, ConfigTableEntrySpec entry)
		{
			return OptionButton.booleanOption(this.screen.width - VALUE_OFFSET, property);
		}
		
		@Override
		public IBetterElement visitNumber(IConfigPrimitive<? extends Number> property, ConfigTableEntrySpec entry)
		{
			return NumberInputField.numberOption(this.screen, this.screen.width - VALUE_OFFSET, entry, property);
		}
		
		@Override
		public IBetterElement visitString(IConfigPrimitive<String> property, ConfigTableEntrySpec entry)
		{
			return StringInputField.stringOption(this.screen, this.screen.width - VALUE_OFFSET, entry, property);
		}
		
		@Override
		public <E extends Enum<E>> IBetterElement visitEnum(IConfigPrimitive<E> property, ConfigTableEntrySpec entry)
		{
			return OptionButton.enumOption(this.screen.width - VALUE_OFFSET, property);
		}
		
		@Override
		public IBetterElement visitUnknown(IConfigPrimitive<?> property, ConfigTableEntrySpec entry)
		{
			return new UnknownOptionWidget(this.screen.width - VALUE_OFFSET, property);
		}
	}
	
	private static class ListElemInfo
	{
		private int index;
		private final ConfigTableEntrySpec parent;
		private final ConfigTableEntrySpec entry;
		
		public ListElemInfo(int index, ConfigTableEntrySpec parent, ConfigTableEntrySpec entry)
		{
			this.index = index;
			this.parent = parent;
			this.entry = entry;
		}
		
		public void updateIndex(int index)
		{
			this.index = index;
			this.entry.setLoc(new ConfigLocation(this.parent.getLoc(), this.parent.getLoc().getName() + "[" + index + "]"));
			this.entry.setDisplayName(new StringTextComponent("[" + index + "]"));
		}
		
		public int getIndex()
		{
			return this.index;
		}
	}
	
	/** An interface for ui elements with a simple layout system */
	private static interface IBetterElement extends IGuiComponent
	{
		/**
		 * Sets the y coordinate of this component to the given one and computes the height
		 * @param y the new y coordinate
		 * @return the computed height of this component
		 */
		int setYgetHeight(int y, ConfigFilter filter);
	}
	
	/** The ui for a group of components */
	private static class UIGroup extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The list of entries of the group */
		private final List<IBetterElement> content;
		private final int width;
		private int height = 0;
		private ILayoutManager layout;

		public UIGroup(int width, List<IBetterElement> content)
		{
			this.content = content;
			this.width = width;
		}

		public void updateLayout()
		{
			if (this.layout != null)
			{
				INestedGuiComponent.super.setLayoutManager(this.layout);
				this.layout.marksLayoutDirty();
			}
		}

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return this.content;
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
			int h = 0;
			for (IBetterElement elem : this.content)
			{
				h += elem.setYgetHeight(y + h, filter);
			}
			this.height = h;
			return h;
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
	}
	
	private static class ListElementEntry extends FocusableGui implements INestedGuiComponent, IBetterElement
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
			this.button = new IconButton(screen, x, 0, 0, new StringTextComponent("X"), deleteAction, new TranslationTextComponent(REMOVE_TOOLTIP_KEY));
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
			this.button.setYgetHeight(y, this.hidden ? ConfigFilter.NONE : filter);
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
	
	/** The ui for a expand/collapse subsection */
	private static class UIFoldout extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The height of the fouldout header */
		private static final int FOLDOUT_HEADER_HEIGHT = 24;
		/** The parent screen */
		private final BetterConfigScreen screen;
		
		/** The edited table */
		private final ConfigTableEntrySpec tableEntry;
		/** The content that will be collapsed */
		private final IBetterElement content;
		/** The extra info to show on the tooltip */
		private final List<ITextProperties> extraInfo = new ArrayList<>();
		/** The layout to notify for layout update */
		private ILayoutManager layout;
		/** The x coordinate of this component */
		private final int baseX;
		/** The y coordinate of this component */
		private int baseY = 0;
		/** The current height of this component */
		private int height = 0;
		
		/** {@code true} when the content is collapsed, {@code false} otherwise */
		private boolean folded = false;
		/** Indicates if the section is hidden or not */
		private boolean hidden = false;

		public UIFoldout(BetterConfigScreen screen, ConfigTableEntrySpec entry, IBetterElement content, int x)
		{
			this.screen = screen;
			this.tableEntry = entry;
			this.content = content;
			this.extraInfo.add(ITextProperties.func_240653_a_(entry.getLoc().getName(), Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
			this.extraInfo.addAll(entry.getDisplayComment());
			this.baseX = x;
		}
		
		// Layout

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return this.folded || this.hidden ? Collections.emptyList() : Arrays.asList(this.content);
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			boolean matchFilter = filter.matches(this.tableEntry);
			this.baseY = y;
			int contentHeight = this.content.setYgetHeight(y + FOLDOUT_HEADER_HEIGHT, matchFilter ? ConfigFilter.ALL : filter);
			
			if (contentHeight == 0)
			{
				// Disable this section
				this.hidden = true;
				this.height = 0;
				updateTexts();
				return 0;
			}
			
			this.hidden = false;

			if (this.folded)
			{
				contentHeight = 0;
			}
			
			this.height = contentHeight + FOLDOUT_HEADER_HEIGHT;
			updateTexts();
			return this.height;
		}
		
		private void updateTexts()
		{
			this.extraInfo.clear();
			this.extraInfo.add(ITextProperties.func_240653_a_(this.tableEntry.getLoc().getName(), Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
			this.extraInfo.addAll(this.tableEntry.getDisplayComment());
		}
		
		@Override
		public void setLayoutManager(ILayoutManager manager)
		{
			this.layout = manager;
			this.content.setLayoutManager(manager);
		}
		
		@Override
		public void onLayoutChanged()
		{
			this.content.onLayoutChanged();
		}

		@Override
		public int getWidth()
		{
			return this.screen.width - X_PADDING - RIGHT_PADDING - this.baseX - this.layout.getLayoutX();
		}

		@Override
		public int getHeight()
		{
			return this.height;
		}
		
		// Mouse interaction
		
		public void toggleFolding()
		{
			this.folded = !this.folded;
			this.layout.marksLayoutDirty();
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if (this.isOverHeader(mouseX, mouseY))
			{
				this.screen.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				this.toggleFolding();
				return true;
			}
			
			if (this.folded)
				return false;
			
			return super.mouseClicked(mouseX, mouseY, button);
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
		
		private boolean isOverHeader(double mouseX, double mouseY)
		{
			if (this.hidden)
				return false;
			
			int y = this.baseY  + this.layout.getLayoutY();
			return mouseX >= this.baseX + this.layout.getLayoutX()
			    && mouseY >= y
			    && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING
			    && mouseY < y + FOLDOUT_HEADER_HEIGHT;
		}
		
		// Rendering
		
		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			if (this.hidden)
				return;
			
			INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
			this.renderFoldoutHeader(matrixStack, mouseX, mouseY, partialTicks);
		}
		
		protected void renderFoldoutHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			int x = this.baseX  + this.layout.getLayoutX();
			int y = this.baseY  + this.layout.getLayoutY();
			// Draw background
			fill(matrixStack, x, y + 2, this.screen.width - X_PADDING - RIGHT_PADDING, y + FOLDOUT_HEADER_HEIGHT - 2, 0xC0_33_33_33);

			// Draw foreground arrow icon
			int arrowU = this.folded ? 16 : 32;
			int arrowV = this.isOverHeader(mouseX, mouseY) ? 16 : 0;
			this.screen.getMinecraft().getTextureManager().bindTexture(BETTER_ICONS);
			blit(matrixStack, x, y + 4, arrowU, arrowV, 16, 16, 256, 256);
			
			// Draw foreground text
			FontRenderer font = this.screen.getFont(); 
			font.drawText(matrixStack, this.tableEntry.getDisplayName(), x + 16, y + 1 + (FOLDOUT_HEADER_HEIGHT - font.FONT_HEIGHT) / 2, 0xFF_FF_FF_FF);
		}
		
		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			if (this.hidden)
				return;
			
			INestedGuiComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
			if (this.isOverHeader(mouseX, mouseY))
			{
				FontRenderer font = this.screen.getFont();
				GuiUtils.drawHoveringText(matrixStack, this.extraInfo, mouseX, mouseY, this.screen.width, this.screen.height, 200, font);
			}
		}
	}
	
	/** The container for the main section */
	private static class UIContainer extends FocusableGui implements INestedGuiComponent
	{
		/** The the height of the header */
		private static final int CONTAINER_HEADER_HEIGHT = 60;
		/** The x position of the input field of the search bar */
		private static final int SEARCH_LABEL_WIDTH = 80;
		/** The parent screen */
		private final BetterConfigScreen screen;
		/** The text field of the search bar */
		private final TextField searchField;
		/** The scroll panel */
		private final UIScrollPane scrollPane;
		/** The tab buttons */
		private final List<IGuiComponent> components = new ArrayList<>();
		/** The filter from the search bar */
		private final ConfigFilter filter = new ConfigFilter();

		public UIContainer(BetterConfigScreen screen, IBetterElement content)
		{
			this.screen = screen;
			int x = X_PADDING;
			
			// Tabs
			int buttonWidth = (this.screen.width - 2 * X_PADDING) / ModConfig.Type.values().length;
			int i = 0;
			for (ModConfig config : screen.getModConfigs())
			{
				final int index = i;
				Button b = new Button(x, Y_PADDING, buttonWidth, 20, new StringTextComponent(config.getFileName()), thisButton -> this.screen.openConfig(index), Button.NO_TOOLTIP);
				b.active = index != screen.getCurrentConfigIndex();
				this.components.add(b);
				
				x += buttonWidth;
				i++;
			}
			
			// Search bar
			this.searchField = new TextField(screen.getFont(), X_PADDING + SEARCH_LABEL_WIDTH + 1, 20 + 2 * Y_PADDING + 1, this.screen.width - 2 * X_PADDING - SEARCH_LABEL_WIDTH - 2, 20 - 2, new TranslationTextComponent(SEARCH_BAR_KEY));
			this.searchField.setResponder(this::updateFilter);
			this.components.add(this.searchField);
			
			// Scroll
			this.scrollPane = new UIScrollPane(screen.getMinecraft(), X_PADDING, Y_PADDING + CONTAINER_HEADER_HEIGHT, screen.width - 2 * X_PADDING, screen.height - 2 * Y_PADDING - CONTAINER_HEADER_HEIGHT, content);
			this.components.add(this.scrollPane);
			this.scrollPane.setYgetHeight(Y_PADDING + CONTAINER_HEADER_HEIGHT, this.filter);
		}
		
		// Layout

		@Override
		public List<? extends IGuiComponent> getEventListeners()
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
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			this.screen.renderBackground(matrixStack);
			INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
			FontRenderer font = this.screen.getFont();
			font.drawText(matrixStack, this.searchField.getMessage(), X_PADDING, 20 + 2 * Y_PADDING + (20 - font.FONT_HEIGHT) / 2, 0xFF_FF_FF_FF);
			this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
		}
		
		protected void renderHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{ }
		
	}
	
	private static class UIScrollPane extends ScrollPane implements IBetterElement
	{
		/** Indicates whether the layout is dirty */
		private boolean dirty = true;
		/** The filter from the search bar */
		private ConfigFilter filter = ConfigFilter.ALL;

		public UIScrollPane(Minecraft minecraft, int x, int y, int w, int h, IBetterElement content)
		{
			super(minecraft, x, y, w, h, content);
		}
		
		// Layout

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.baseY = y;
			this.filter = filter;
			this.checkLayout();
			
			return this.getHeight();
		}
		
		protected void checkLayout()
		{
			if (this.isDirty())
			{
				((IBetterElement)this.content).setYgetHeight(0, this.filter);
				this.clean();
			}
		}
		
		@Override
		public void marksLayoutDirty()
		{
			this.dirty = true;
		}
		
		/** Marks the layout as not dirty */
		private void clean()
		{
			this.dirty = false;
		}
		
		/** Indicates whether the layout is dirty */
		private boolean isDirty()
		{
			return this.dirty;
		}
		
		// User interaction
		
		@Override
		public void mouseMoved(double mouseX, double mouseY)
		{
			super.mouseMoved(mouseX, mouseY);
			this.checkLayout();
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			boolean res = super.mouseClicked(mouseX, mouseY, button);
			this.checkLayout();
			return res;
		}
		
		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button)
		{
			boolean res = super.mouseReleased(mouseX, mouseY, button);
			this.checkLayout();
			return res;
		}
		
		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
		{
			boolean res = super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
			this.checkLayout();
			return res;
		}
		
		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double delta)
		{
			boolean res = super.mouseScrolled(mouseX, mouseY, delta);
			this.checkLayout();
			return res;
		}
		
		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers)
		{
			boolean res = super.keyPressed(keyCode, scanCode, modifiers);
			this.checkLayout();
			return res;
		}
		
		@Override
		public boolean keyReleased(int keyCode, int scanCode, int modifiers)
		{
			boolean res = super.keyReleased(keyCode, scanCode, modifiers);
			this.checkLayout();
			return res;
		}
		
		@Override
		public boolean charTyped(char codePoint, int modifiers)
		{
			boolean res = super.charTyped(codePoint, modifiers);
			this.checkLayout();
			return res;
		}
		
		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			if (this.isMouseOver(mouseX, mouseY))
				super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	private static class BetterButton extends Button implements IBetterElement
	{
		/** The parent screen */
		protected final BetterConfigScreen screen;
		
		private final List<? extends ITextProperties> tooltipInfo;
		
		public BetterButton(BetterConfigScreen screen, int xPos, int width, ITextComponent displayString, IPressable pressedHandler, ITextComponent overlay)
		{
			super(xPos, 0, width, VALUE_HEIGHT, displayString, pressedHandler, null);
			this.screen = screen;
			this.tooltipInfo = Arrays.asList(overlay);
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.setY(y + (VALUE_CONTAINER_HEIGHT - VALUE_HEIGHT) / 2);
			return VALUE_CONTAINER_HEIGHT;
		}
		
		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			if (this.isMouseOver(mouseX, mouseY))
			{
				FontRenderer font = Minecraft.getInstance().fontRenderer;
				GuiUtils.drawHoveringText(matrixStack, this.tooltipInfo, mouseX, mouseY, this.screen.width, this.screen.height, 200, font);
			}
		}
	}
	
	private static class IconButton extends BetterButton
	{
		private final int iconU;
		private final int iconV;

		public IconButton(BetterConfigScreen screen, int xPos, int iconU, int iconV, ITextComponent displayString, IPressable pressedHandler, ITextComponent overlay)
		{
			super(screen, xPos, VALUE_HEIGHT, displayString, pressedHandler, overlay);
			this.iconU = iconU;
			this.iconV = iconV;
		}
		
		@Override
		public void renderWidget(MatrixStack mStack, int mouseX, int mouseY, float partial)
		{
			// Draw foreground icon
			int v = this.iconV + (this.isHovered ? 16 : 0);
			this.screen.getMinecraft().getTextureManager().bindTexture(BETTER_ICONS);
			blit(mStack, x + (VALUE_HEIGHT - 16) / 2, y + (VALUE_HEIGHT - 16) / 2, this.iconU, v, 16, 16, 256, 256);
		}
		
	}
	
	//TODO [1.0] Add reset button
	/** The container for table entries */
	private static class ValueContainer extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The parent screen */
		private final BetterConfigScreen screen;
		/** The edited property */
		private final ConfigTableEntrySpec entry;
		private final IConfigPrimitive<?> property;
		/** the value widget */
		private final IBetterElement content;
		/** The title of the property */
		private List<IReorderingProcessor> nameLines;
		/** The extra info to show on the tooltip */
		private final List<ITextProperties> extraInfo = new ArrayList<>();
		/** The parent layout */
		private ILayoutManager layout = ILayoutManager.NONE;
		/** The x coordinate of the component */
		private final int baseX;
		/** The y coordinate of the component */
		private int baseY = 0;
		/** The height of the component */
		private int height = 0;
		/** Indicates if the property is hidden or not */
		private boolean hidden = false;

		public ValueContainer(BetterConfigScreen screen, ConfigTableEntrySpec entry, IConfigPrimitive<?> property, IBetterElement content, int x)
		{
			this.screen = screen;
			this.entry = entry;
			this.property = property;
			this.content = content;
			this.baseX = x;
			this.updateTexts();
		}
		
		// Layout

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return this.hidden ? Collections.emptyList() : Arrays.asList(this.content);
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.baseY = y;
			this.hidden = !filter.matches(this.entry);
			
			if (this.hidden)
			{
				this.height = 0;
				return 0;
			}
			
			this.height = Math.max(VALUE_CONTAINER_HEIGHT, this.nameLines.size() * this.screen.getFont().FONT_HEIGHT);
			this.content.setYgetHeight(y + (this.height - VALUE_HEIGHT) / 2, ConfigFilter.ALL);
			updateTexts();
			return this.height;
		}
		
		private void updateTexts()
		{
			FontRenderer font = this.screen.getFont();
			this.nameLines = font.trimStringToWidth(this.entry.getDisplayName(), this.screen.width - this.baseX - VALUE_WIDTH - 2 * X_PADDING - RIGHT_PADDING - 4);
			this.extraInfo.clear();
			this.extraInfo.add(ITextProperties.func_240653_a_(this.entry.getLoc().getName(), Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
			this.extraInfo.addAll(this.entry.getDisplayComment());
			this.extraInfo.add((new TranslationTextComponent(DEFAULT_VALUE_KEY, new StringTextComponent(Objects.toString(this.property.getSpec().getDefaultValue())))).mergeStyle(TextFormatting.GRAY));
		}

		@Override
		public void setLayoutManager(ILayoutManager manager)
		{
			this.layout = manager;
			this.content.setLayoutManager(manager);
		}

		@Override
		public int getWidth()
		{
			return this.screen.width - X_PADDING - RIGHT_PADDING - this.baseX - this.layout.getLayoutY();
		}

		@Override
		public int getHeight()
		{
			return this.height;
		}
		
		@Override
		public boolean isMouseOver(double mouseX, double mouseY)
		{
			if (this.hidden)
				return false;
			
			int y = this.baseY + this.layout.getLayoutY();
			return mouseX >= this.baseX + this.layout.getLayoutX() && mouseY >= y && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING && mouseY < y + this.height;
		}
		
		// Rendering
		
		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			if (this.hidden)
				return;
			
			INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
			FontRenderer font = this.screen.getFont();
			int y = this.baseY + this.layout.getLayoutY() + (this.height - this.nameLines.size() * this.screen.getFont().FONT_HEIGHT) / 2 + 1;
			for(IReorderingProcessor line : this.nameLines)
			{
				font.func_238422_b_(matrixStack, line, this.baseX + this.layout.getLayoutX() + 1, y, 0xFF_FF_FF_FF);
				y += 9;
			}
		}
		
		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			if (this.hidden)
				return;
			
			INestedGuiComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
			if (this.isMouseOver(mouseX, mouseY))
			{
				FontRenderer font = this.screen.getFont();
				int yOffset = 0;
				if (mouseX >= this.screen.width - X_PADDING - VALUE_WIDTH - RIGHT_PADDING)
					yOffset = 24; // Fixes the overlay text covering the text on the content
				
				GuiUtils.drawHoveringText(matrixStack, this.extraInfo, mouseX, mouseY + yOffset, this.screen.width, this.screen.height, 200, font);
			}
		}
	}
	
	/** The widget for option buttons */
	private static class OptionButton<V> extends CycleOptionButton<V> implements IBetterElement
	{
		private OptionButton(int xPos, List<? extends V> acceptedValues,
			Function<? super V, ITextComponent> valueToText, IConfigPrimitive<V> property)
		{
			super(xPos, 0, VALUE_WIDTH, VALUE_HEIGHT,
				acceptedValues.stream().filter(property.getSpec()::isAllowed).collect(Collectors.toList()),
				valueToText,
				property.getValue(), thiz -> property.setValue(thiz.getCurrentValue()),
				NO_TOOLTIP);
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.setY(y);
			return this.height;
		}
		
		/** Creates a widget for boolean values */
		private static OptionButton<Boolean> booleanOption(int xPos, IConfigPrimitive<Boolean> property)
		{
			return new OptionButton<>(
				xPos,
				Arrays.asList(false, true),
				bool -> new TranslationTextComponent(bool ? TRUE_OPTION_KEY : FALSE_OPTION_KEY),
				property);
		}

		/** Creates a widget for enum values */
		@SuppressWarnings("unchecked")
		private static <E extends Enum<E>> OptionButton<E> enumOption(int xPos, IConfigPrimitive<E> property)
		{
			return new OptionButton<>(
				xPos,
				Arrays.asList(((Class<E>)property.getSpec().getValueClass()).getEnumConstants()),
				enuw -> new StringTextComponent(enuw.name()),
				property);
		}
	}

	/** The widget for string properties */
	private static class StringInputField extends TextField implements IBetterElement
	{
		/** The property to edit */
		private final IConfigPrimitive<String> property;
		
		private StringInputField(FontRenderer fontRenderer, int x, IConfigPrimitive<String> property, ITextComponent title)
		{
			super(fontRenderer, x + 1, 0, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, title);
			this.property = property;
			this.setText(property.getValue());
			this.setResponder(this::updateTextColor);
		}
		
		/** Updates the color of the text to indicates an error */
		private void updateTextColor(String text)
		{
			this.setTextColor(this.property.getSpec().isAllowed(text) ? DEFAULT_FIELD_TEXT_COLOR : ERROR_FIELD_TEXT_COLOR);
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.setY(y + 1);
			return this.height + 2;
		}
		
		@Override
		protected void onValidate(String text)
		{
			if (this.property.getSpec().isAllowed(text))
			{
				this.property.setValue(text);
			}
		}

		/** Creates a widget for string values */
		private static StringInputField stringOption(BetterConfigScreen screen, int xPos, ConfigTableEntrySpec entry, IConfigPrimitive<String> property)
		{
			return new StringInputField(screen.getFont(), xPos, property, entry.getDisplayName());
		}
	}

	/** The widget for number properties */
	private static class NumberInputField<N extends Number> extends NumberField<N> implements IBetterElement
	{
		/** The property to edit */
		private final IConfigPrimitive<N> property;

		public NumberInputField(FontRenderer fontRenderer, int x, INumberType<N> numberType, IConfigPrimitive<N> property, ITextComponent title)
		{
			super(fontRenderer, x, 0, VALUE_WIDTH, VALUE_HEIGHT, title, numberType, property.getValue());
			this.property = property;
			this.inputField.setResponder(this::updateTextColor);
		}

		/** Updates the color of the text to indicates an error */
		private void updateTextColor(String text)
		{
			this.inputField.setTextColor(this.property.getSpec().isAllowed(this.getValue()) ? DEFAULT_FIELD_TEXT_COLOR : ERROR_FIELD_TEXT_COLOR);
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.setY(y);
			return VALUE_HEIGHT;
		}
		
		@Override
		protected N correct(N value)
		{
			if (this.property.getSpec().isAllowed(value))
				return value;
			
			return this.property.getSpec().correct(value);
		}
		
		@Override
		protected void onValidate(N value)
		{
			if (this.property.getSpec().isAllowed(value))
			{
				this.property.setValue(value);
			}
		}

		/** Creates a widget for number values */
		@SuppressWarnings("unchecked")
		private static <N extends Number> NumberInputField<N> numberOption(BetterConfigScreen screen, int xPos, ConfigTableEntrySpec entry, IConfigPrimitive<N> property)
		{
			return new NumberInputField<>(screen.getFont(), xPos, NumberTypes.getType((Class<N>)property.getSpec().getValueClass()), property, entry.getDisplayName());
		}
	}

	/** The widget for properties of unknown type */
	private static class UnknownOptionWidget extends Button implements IBetterElement
	{
		private UnknownOptionWidget(int xPos, IConfigPrimitive<?> property)
		{
			super(xPos, 0, VALUE_WIDTH, VALUE_HEIGHT, new StringTextComponent(Objects.toString(property.getValue())), thiz -> {}, NO_TOOLTIP);
			this.active = false;
		}

		@Override
		public int setYgetHeight(int y, ConfigFilter filter)
		{
			this.y = y;
			return VALUE_HEIGHT;
		}
	}
	
}

package fr.max2.betterconfig.client.gui.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.ConfigProperty;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.config.ModConfig;

/** A builder for better configuration screen */
public class BetterConfigBuilder implements IConfigUIBuilder<BetterConfigBuilder.IBetterElement>
{
	/** The translation key for displaying the default value */
	public static final String DEFAULT_VALUE_KEY = BetterConfig.MODID + ".option.default_value";
	
	/** The width of the indentation added for each nested section */
	private static final int SECTION_TAB_SIZE = 20;
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

	@Override
	public ITableUIBuilder<IBetterElement> start(BetterConfigScreen screen)
	{
		return new Table(screen, content -> new UIContainer(screen, content), 0);
	}
	
	private static class Table implements ITableUIBuilder<IBetterElement>
	{
		/** The parent screen */
		private final BetterConfigScreen screen;
		/** The function to call to finalize the creation of the table ui */
		private final UnaryOperator<IBetterElement> finalize;
		/** The x offset of the created elements */
		private final int xOffset;

		private Table(BetterConfigScreen screen, UnaryOperator<IBetterElement> finalize, int xOffset)
		{
			this.screen = screen;
			this.finalize = finalize;
			this.xOffset = xOffset;
		}

		@Override
		public ITableUIBuilder<IBetterElement> subTableBuilder(String path, String comment)
		{
			return new Table(this.screen, content -> new UIFoldout(this.screen, content, path, linesToTextProperties(splitText(comment)), this.xOffset), this.xOffset + SECTION_TAB_SIZE);
		}

		@Override
		public IValueUIBuilder<IBetterElement> tableEntryBuilder(String path, String comment)
		{
			return new Value(this, path, comment);
		}

		@Override
		public IBetterElement buildTable(List<IBetterElement> tableContent)
		{
			return this.finalize.apply(new UITable(this.screen.width - 2 * X_PADDING - RIGHT_PADDING - this.xOffset,tableContent));
		}
	}
	
	private static class Value implements IValueUIBuilder<IBetterElement>
	{
		/** The parent builder */
		private final Table parent;
		
		private static final int OFFSET = 2 * X_PADDING + RIGHT_PADDING + VALUE_WIDTH + 4;

		private Value(Table parent, String path, String comment)
		{
			this.parent = parent;
		}

		@Override
		public IBetterElement buildBoolean(ConfigProperty<Boolean> property)
		{
			return finalizeBuild(OptionButton.booleanOption(this.parent.screen.width - OFFSET, property), property);
		}

		@Override
		public IBetterElement buildNumber(ConfigProperty<? extends Number> property)
		{
			return finalizeBuild(NumberInputField.numberOption(this.parent.screen, this.parent.screen.width - OFFSET, property), property);
		}

		@Override
		public IBetterElement buildString(ConfigProperty<String> property)
		{
			return finalizeBuild(StringInputField.stringOption(this.parent.screen, this.parent.screen.width - OFFSET, property), property);
		}

		@Override
		public <E extends Enum<E>> IBetterElement buildEnum(ConfigProperty<E> property)
		{
			return finalizeBuild(OptionButton.enumOption(this.parent.screen.width - OFFSET, property), property);
		}

		@Override
		public IBetterElement buildList(ConfigProperty<? extends List<?>> property)
		{
			// TODO Implement list config ui
			return buildUnknown(property);
		}

		@Override
		public IBetterElement buildUnknown(ConfigProperty<?> property)
		{
			return finalizeBuild(new UnknownOptionWidget(this.parent.screen.width - OFFSET, property), property);
		}
		
		/** Finalizes the creating of a property edit ui element */
		private IBetterElement finalizeBuild(IBetterElement content, ConfigProperty<?> property)
		{
			return new ValueContainer(this.parent.screen, content, property, this.parent.xOffset);
		}
	}
	
	/** Splits the given text to the list of contained lines */
	private static List<String> splitText(String text)
	{
		if (Strings.isNullOrEmpty(text))
			return Collections.emptyList();
		
		return Arrays.asList(text.split("\n"));
	}
	
	/** Convert the given text into a list of text properties to render */
	private static List<? extends ITextProperties> linesToTextProperties(List<String> text)
	{
		return text.stream().map(ITextProperties::func_240652_a_).collect(Collectors.toList());
	}
	
	/** An interface for ui elements with a simple layout system */
	public static interface IBetterElement extends IGuiComponent
	{
		// TODO add filter parameter for search bar
		/**
		 * Sets the y coordinate of this component to the given one and computes the height
		 * @param y the new y coordinate
		 * @return the computed height of this component
		 */
		int setYgetHeight(int y);
	}
	
	/** The ui for a config table */
	private static class UITable extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The list of entries of the table */
		private final List<IBetterElement> content;
		private final int width;
		private int height = 0;

		public UITable(int width, List<IBetterElement> content)
		{
			this.content = content;
			this.width = width;
		}

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return this.content;
		}
		
		@Override
		public int setYgetHeight(int y)
		{
			int h = 0;
			for (IBetterElement elem : this.content)
			{
				h += elem.setYgetHeight(y + h);
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
	
	/** The ui for a expand/collapse subsection */
	private static class UIFoldout extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The height of the fouldout header */
		private static final int FOLDOUT_HEADER_HEIGHT = 20;
		/** The parent screen */
		private final BetterConfigScreen screen;
		/** The content that will be collapsed */
		private final IBetterElement content;
		/** The title on the header */
		private final IReorderingProcessor sectionName;
		/** The extra info to show on the tooltip */
		private final List<? extends ITextProperties> extraInfo;
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

		public UIFoldout(BetterConfigScreen screen, IBetterElement content, String path, List<? extends ITextProperties> comment, int x)
		{
			this.screen = screen;
			this.content = content;
			this.sectionName = IReorderingProcessor.fromString(path, Style.EMPTY.mergeWithFormatting(TextFormatting.BOLD, TextFormatting.YELLOW));
			this.extraInfo = comment;
			this.baseX = x;
		}
		
		// Layout

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return this.folded ? Collections.emptyList() : Arrays.asList(this.content);
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.baseY = y;
			int contentHeight = this.content.setYgetHeight(y + FOLDOUT_HEADER_HEIGHT);

			if (this.folded)
			{
				contentHeight = 0;
			}
			
			this.height = contentHeight + FOLDOUT_HEADER_HEIGHT;
			return this.height;
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
			int y = this.baseY  + this.layout.getLayoutY();
			return mouseX >= this.baseX + this.layout.getLayoutX()
			    && mouseY >= y
			    && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING
			    && mouseY < y + this.height;
		}
		
		private boolean isOverHeader(double mouseX, double mouseY)
		{
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
			INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
			this.renderFoldoutHeader(matrixStack, mouseX, mouseY, partialTicks);
		}
		
		protected void renderFoldoutHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			int x = this.baseX  + this.layout.getLayoutX();
			int y = this.baseY  + this.layout.getLayoutY();
			// Draw background
			fill(matrixStack, x, y, this.screen.width - X_PADDING - RIGHT_PADDING, y + FOLDOUT_HEADER_HEIGHT, 0xC0_33_33_33);
			// Draw foreground
			String arrow = this.folded ? ">" : "v";
			FontRenderer font = this.screen.getFont(); 
			font.drawString(matrixStack, arrow, x + 1, y + (FOLDOUT_HEADER_HEIGHT - font.FONT_HEIGHT) / 2, 0xFF_FF_FF_FF);
			font.func_238422_b_(matrixStack, this.sectionName, x + 11, y + (FOLDOUT_HEADER_HEIGHT - font.FONT_HEIGHT) / 2, 0xFF_FF_FF_FF);
		}
		
		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			INestedGuiComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
			if (this.isOverHeader(mouseX, mouseY))
			{
				FontRenderer font = this.screen.getFont();
				GuiUtils.drawHoveringText(matrixStack, this.extraInfo, mouseX, mouseY, this.screen.width, this.screen.height, 200, font);
			}
		}
	}
	
	//TODO add search bar
	/** The container for the main section */
	private static class UIContainer extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The the height of the header */
		private static final int CONTAINER_HEADER_HEIGHT = 30;
		/** The parent screen */
		private final BetterConfigScreen screen;
		/** The contained section */
		private final IBetterElement content;
		/** The tab buttons */
		private final List<IGuiComponent> configTabs = new ArrayList<>();
		/** The y coordinate of this widget */
		private int y;

		public UIContainer(BetterConfigScreen screen, IBetterElement content)
		{
			this.screen = screen;
			this.content = new UIScrollPane(screen.getMinecraft(), X_PADDING, Y_PADDING + CONTAINER_HEADER_HEIGHT, screen.width - 2 * X_PADDING, screen.height - 2 * Y_PADDING - CONTAINER_HEADER_HEIGHT, content);
			int x = X_PADDING;
			int buttonWidth = (this.screen.width - 2 * X_PADDING) / ModConfig.Type.values().length;
			int i = 0;
			for (ModConfig config : screen.getModConfigs())
			{
				final int index = i;
				Button b = new Button(x, Y_PADDING, buttonWidth, 20, new StringTextComponent(config.getFileName()), thisButton -> this.screen.openConfig(index), Button.NO_TOOLTIP);
				b.active = index != screen.getCurrentConfigIndex();
				this.configTabs.add(b);
				
				x += buttonWidth;
				i++;
			}
			this.configTabs.add(this.content);
			this.setYgetHeight(Y_PADDING);
		}
		
		// Layout

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return this.configTabs;
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.y = y;
			this.content.setYgetHeight(y + CONTAINER_HEADER_HEIGHT);
			return this.getHeight(); 
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
		
		// Rendering
		
		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			this.screen.renderBackground(matrixStack);
			INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
			this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
		}
		
		protected void renderHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{ }
		
	}
	
	private static class UIScrollPane extends ScrollPane implements IBetterElement
	{
		/** Indicates whether the layout is dirty */
		private boolean dirty = true;

		public UIScrollPane(Minecraft minecraft, int x, int y, int w, int h, IBetterElement content)
		{
			super(minecraft, x, y, w, h, content);
		}
		
		// Layout

		@Override
		public int setYgetHeight(int y)
		{
			this.baseY = y;
			this.checkLayout();
			
			return this.getHeight();
		}
		
		protected void checkLayout()
		{
			if (this.isDirty())
			{
				((IBetterElement)this.content).setYgetHeight(0);
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
		
		/*@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			checkLayout();
			super.render(matrixStack, mouseX, mouseY, partialTicks);
		}*/
		
	}
	
	/** The container for table entries */
	private static class ValueContainer extends FocusableGui implements INestedGuiComponent, IBetterElement
	{
		/** The parent screen */
		private final BetterConfigScreen screen;
		/** the value widget */
		private final IBetterElement content;
		/** The title of the property */
		private final List<IReorderingProcessor> nameLines;
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

		public ValueContainer(BetterConfigScreen screen, IBetterElement content, ConfigProperty<?> property, int x)
		{
			this.screen = screen;
			this.content = content;
			FontRenderer font = this.screen.getFont();
			this.nameLines = font.trimStringToWidth(property.getDisplayName(), screen.width - x - VALUE_WIDTH - 2 * X_PADDING - RIGHT_PADDING - 4);
			List<String> path = property.getPath();
			if (!path.isEmpty())
				this.extraInfo.add(ITextProperties.func_240653_a_(path.get(path.size() - 1), Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
			this.extraInfo.addAll(property.getDisplayComment());
			this.extraInfo.add((new TranslationTextComponent(DEFAULT_VALUE_KEY, new StringTextComponent(property.getDefaultValue().toString()))).mergeStyle(TextFormatting.GRAY));
			this.baseX = x;
		}
		
		// Layout

		@Override
		public List<? extends IGuiComponent> getEventListeners()
		{
			return Arrays.asList(this.content);
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.baseY = y;
			this.height = Math.max(VALUE_CONTAINER_HEIGHT, this.nameLines.size() * this.screen.getFont().FONT_HEIGHT);
			this.content.setYgetHeight(y + (this.height - VALUE_HEIGHT) / 2);
			return this.height;
		}

		@Override
		public void setLayoutManager(ILayoutManager manager)
		{
			this.layout = manager;
			INestedGuiComponent.super.setLayoutManager(manager);
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
			int y = this.baseY + this.layout.getLayoutY();
			return mouseX >= this.baseX + this.layout.getLayoutX() && mouseY >= y && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING && mouseY < y + this.height;
		}
		
		// Rendering
		
		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
			FontRenderer font = this.screen.getFont();
			int y = this.baseY + this.layout.getLayoutY() + (this.height - this.nameLines.size() * this.screen.getFont().FONT_HEIGHT) / 2;
			for(IReorderingProcessor line : this.nameLines)
			{
				font.func_238422_b_(matrixStack, line, this.baseX + this.layout.getLayoutX() + 1, y, 0xFF_FF_FF_FF);
				y += 9;
			}
		}
		
		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
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
			Function<? super V, ITextComponent> valueToText, ConfigProperty<V> property)
		{
			super(xPos, 0, VALUE_WIDTH, VALUE_HEIGHT,
				acceptedValues.stream().filter(property::isAllowed).collect(Collectors.toList()),
				valueToText,
				property.getValue(), thiz -> property.setValue(thiz.getCurrentValue()),
				NO_TOOLTIP);
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.setY(y);
			return this.height;
		}
		
		/** Creates a widget for boolean values */
		private static OptionButton<Boolean> booleanOption(int xPos, ConfigProperty<Boolean> property)
		{
			return new OptionButton<>(
				xPos,
				Arrays.asList(false, true),
				bool -> new TranslationTextComponent(bool ? TRUE_OPTION_KEY : FALSE_OPTION_KEY),
				property);
		}

		/** Creates a widget for enum values */
		@SuppressWarnings("unchecked")
		private static <E extends Enum<E>> OptionButton<E> enumOption(int xPos, ConfigProperty<E> property)
		{
			return new OptionButton<>(
				xPos,
				Arrays.asList(((Class<E>)property.getValueClass()).getEnumConstants()),
				enuw -> new StringTextComponent(enuw.name()),
				property);
		}
	}

	/** The widget for string properties */
	private static class StringInputField extends TextField implements IBetterElement
	{
		/** The property to edit */
		private final ConfigProperty<String> property;
		
		private StringInputField(FontRenderer fontRenderer, int x, ConfigProperty<String> property)
		{
			super(fontRenderer, x + 1, 0, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, property.getDisplayName());
			this.property = property;
			this.setText(property.getValue());
			this.setResponder(this::updateTextColor);
		}
		
		/** Updates the color of the text to indicates an error */
		private void updateTextColor(String text)
		{
			this.setTextColor(this.property.isAllowed(text) ? DEFAULT_FIELD_TEXT_COLOR : ERROR_FIELD_TEXT_COLOR);
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.setY(y + 1);
			return this.height + 2;
		}
		
		@Override
		protected void onValidate(String text)
		{
			if (this.property.isAllowed(text))
			{
				this.property.setValue(text);
			}
		}

		/** Creates a widget for string values */
		private static StringInputField stringOption(BetterConfigScreen screen, int xPos, ConfigProperty<String> property)
		{
			return new StringInputField(screen.getFont(), xPos, property);
		}
	}

	/** The widget for number properties */
	private static class NumberInputField<N extends Number> extends NumberField<N> implements IBetterElement
	{
		/** The property to edit */
		private final ConfigProperty<N> property;

		public NumberInputField(FontRenderer fontRenderer, int x, INumberType<N> numberType, ConfigProperty<N> property)
		{
			super(fontRenderer, x, 0, VALUE_WIDTH, VALUE_HEIGHT, property.getDisplayName(), numberType, property.getValue());
			this.property = property;
			this.inputField.setResponder(this::updateTextColor);
		}

		/** Updates the color of the text to indicates an error */
		private void updateTextColor(String text)
		{
			this.inputField.setTextColor(this.property.isAllowed(this.getValue()) ? DEFAULT_FIELD_TEXT_COLOR : ERROR_FIELD_TEXT_COLOR);
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.setY(y);
			return VALUE_HEIGHT;
		}
		
		@Override
		protected N correct(N value)
		{
			if (this.property.isAllowed(value))
				return value;
			
			return this.property.correct(value);
		}
		
		@Override
		protected void onValidate(N value)
		{
			if (this.property.isAllowed(value))
			{
				this.property.setValue(value);
			}
		}

		/** Creates a widget for number values */
		@SuppressWarnings("unchecked")
		private static <N extends Number> NumberInputField<N> numberOption(BetterConfigScreen screen, int xPos, ConfigProperty<N> property)
		{
			return new NumberInputField<>(screen.getFont(), xPos, NumberTypes.getType((Class<N>)property.getValueClass()), property);
		}
	}

	/** The widget for properties of unknown type */
	private static class UnknownOptionWidget extends Button implements IBetterElement
	{
		private UnknownOptionWidget(int xPos, ConfigProperty<?> property)
		{
			super(xPos, 0, VALUE_WIDTH, VALUE_HEIGHT, new StringTextComponent(property.getValue().toString()), thiz -> {}, NO_TOOLTIP);
			this.active = false;
		}

		@Override
		public int setYgetHeight(int y)
		{
			this.y = y;
			return VALUE_HEIGHT;
		}
	}
	
}

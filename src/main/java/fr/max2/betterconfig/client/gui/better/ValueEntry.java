package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The container for table entries */
public class ValueEntry extends FocusableGui implements INestedGuiComponent, IBetterElement
{
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The edited property */
	private final IConfigPrimitive<?> property;
	private final IBetterElement content;
	private final IBetterElement button;
	private final List<IBetterElement> children;
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

	public ValueEntry(BetterConfigScreen screen, IConfigPrimitive<?> property, IBetterElement content, int x)
	{
		this.screen = screen;
		this.property = property;
		this.content = content;
		// TODO [1.0] Actually make the undo button undo the change
		// TODO [2.0] Gray out the button when value is unchanged
		// TODO [2.0] Add reset to default button
		this.button = new BetterButton.Icon(screen, this.screen.width - 2 * X_PADDING - RIGHT_PADDING - VALUE_HEIGHT - 4, 48, 0, new TranslationTextComponent(UNDO_TOOLTIP_KEY), thiz ->
		{
			property.undoChanges();
		}, new TranslationTextComponent(UNDO_TOOLTIP_KEY));
		this.children = Arrays.asList(content, this.button);
		this.baseX = x;
		this.updateTexts();
	}
	
	// Layout

	@Override
	public List<? extends IGuiComponent> getEventListeners()
	{
		return this.hidden ? Collections.emptyList() : this.children;
	}

	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		this.baseY = y;
		this.hidden = !filter.matches(this.property);
		
		if (this.hidden)
		{
			this.height = 0;
			return 0;
		}

		updateTexts();
		this.height = Math.max(VALUE_CONTAINER_HEIGHT, this.nameLines.size() * this.screen.getFont().FONT_HEIGHT);
		for (IBetterElement elem : this.children)
		{
			elem.setYgetHeight(y + (this.height - VALUE_HEIGHT) / 2, ConfigFilter.ALL);
		}
		return this.height;
	}
	
	private void updateTexts()
	{
		FontRenderer font = this.screen.getFont();
		this.nameLines = font.trimStringToWidth(this.property.getDisplayName(), this.screen.width - this.baseX - VALUE_WIDTH - 2 * X_PADDING - RIGHT_PADDING - VALUE_HEIGHT - 4);
		this.extraInfo.clear();
		this.extraInfo.add(ITextProperties.func_240653_a_(this.property.getName(), Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
		this.extraInfo.addAll(this.property.getDisplayComment());
		this.extraInfo.add((new TranslationTextComponent(DEFAULT_VALUE_KEY, new StringTextComponent(Objects.toString(this.property.getSpec().getDefaultValue())))).mergeStyle(TextFormatting.GRAY));
	}

	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		for (IBetterElement elem : this.children)
		{
			elem.setLayoutManager(manager);
		}
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
		
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
		if (this.isMouseOver(mouseX, mouseY))
			this.button.render(matrixStack, mouseX, mouseY, partialTicks);
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
		int y = this.baseY + this.layout.getLayoutY();
		if ( mouseX >= this.baseX + this.layout.getLayoutX() && mouseY >= y && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING - VALUE_HEIGHT && mouseY < y + this.height)
		{
			FontRenderer font = this.screen.getFont();
			int yOffset = 0;
			if (mouseX >= this.screen.width - X_PADDING - VALUE_WIDTH - RIGHT_PADDING - VALUE_HEIGHT)
				yOffset = 24; // Fixes the overlay text covering the text on the content
			
			GuiUtils.drawHoveringText(matrixStack, this.extraInfo, mouseX, mouseY + yOffset, this.screen.width, this.screen.height, 200, font);
		}
	}
}
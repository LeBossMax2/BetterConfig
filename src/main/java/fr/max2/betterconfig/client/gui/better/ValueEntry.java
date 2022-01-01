package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.client.gui.component.widget.TextOverlay;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

/** The container for table entries */
public class ValueEntry extends CompositeComponent implements IBetterElement
{
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The edited property */
	private final IConfigPrimitive<?> property;
	/** The title of the property */
	private List<FormattedCharSequence> nameLines;
	/** The extra info to show on the tooltip */
	private final List<Component> extraInfo = new ArrayList<>();
	/** Indicates if the property is hidden or not */
	private boolean filteredOut = false;

	public ValueEntry(BetterConfigScreen screen, IConfigPrimitive<?> property, IComponent content)
	{
		super("better:value_entry");
		this.screen = screen;
		this.property = property;
		// TODO [#2] Gray out the button when value is unchanged
		// TODO [#2] Add reset to default button
		IComponent undoButton = new BetterButton.Icon(screen, 48, 0, new TranslatableComponent(GuiTexts.UNDO_TOOLTIP_KEY), thiz ->
		{
			property.undoChanges();
		}, new TranslatableComponent(GuiTexts.UNDO_TOOLTIP_KEY)).addClass("better:undo");
		IComponent spacing = new UnitComponent("spacing")
		{
			// TODO [#2] Use alignment instead of spacing component
			@Override
			protected void onRender(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks)
			{ }
			
			@Override
			public boolean isActive()
			{
				return false;
			}
			
			@Override
			public boolean hasFocus()
			{
				return false;
			}
			
			@Override
			public void updateNarration(NarrationElementOutput narrationOutput)
			{ }
		};
		this.children.addAll(Arrays.asList(spacing, content, undoButton));
		this.registerProperty(FILTERED_OUT, () -> this.filteredOut);
		
		this.overlay = new TextOverlay(screen, this.extraInfo)
		{
			@Override
			public void onTooltip(Button button, PoseStack matrixStack, int mouseX, int mouseY)
			{
				int yOffset = 0;
				if (content.isHovered())
					yOffset = 24; // Fixes the overlay text covering the text of the content
				
				super.onTooltip(button, matrixStack, mouseX, mouseY + yOffset);
			}
		};
	}
	
	// Layout
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		this.filteredOut = !filter.matches(this.property);
		return this.filteredOut;
	}
	
	@Override
	public void computeLayout(Rectangle availableRect)
	{
		super.computeLayout(availableRect);
		updateTexts();
	}
	
	private void updateTexts()
	{
		Font font = this.screen.getFont();
		this.nameLines = font.split(this.property.getDisplayName(), this.screen.width /*- this.getRect().x*/ - Constants.VALUE_WIDTH - Constants.X_PADDING - Constants.RIGHT_PADDING - Constants.VALUE_HEIGHT - 4);
		this.extraInfo.clear();
		this.extraInfo.add(new TextComponent(this.property.getName()).withStyle(ChatFormatting.YELLOW));
		this.extraInfo.addAll(this.property.getDisplayComment());
		this.extraInfo.add((new TranslatableComponent(GuiTexts.DEFAULT_VALUE_KEY, new TextComponent(Objects.toString(this.property.getSpec().getDefaultValue())))).withStyle(ChatFormatting.GRAY));
	}
	
	// Rendering
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		Rectangle rect = this.getRect();
		
		super.onRender(matrixStack, mouseX, mouseY, partialTicks);
		Font font = this.screen.getFont();
		int y = rect.y + (rect.size.height - this.nameLines.size() * this.screen.getFont().lineHeight) / 2 + 1;
		for(FormattedCharSequence line : this.nameLines)
		{
			font.draw(matrixStack, line, rect.x + 1, y, 0xFF_FF_FF_FF);
			y += 9;
		}
	}
	
	@Override
	public void invalidate()
	{
		this.children.forEach(IComponent::invalidate);
	}
}
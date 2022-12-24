package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.widget.Text;
import fr.max2.betterconfig.client.gui.component.widget.TextOverlay;
import fr.max2.betterconfig.client.gui.layout.Alignment;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/** The container for table entries */
public class ValueEntry extends CompositeComponent implements IBetterElement
{
	private final IConfigName identifier;
	/** The edited property */
	private final IConfigPrimitive<?> property;
	/** The extra info to show on the tooltip */
	private final List<Component> extraInfo = new ArrayList<>();
	/** Indicates if the property is hidden or not */
	private boolean filteredOut = false;

	public ValueEntry(BetterConfigScreen screen, IConfigName identifier, IConfigPrimitive<?> property, IComponent content)
	{
		super("better:value_entry");
		this.identifier = identifier;
		this.property = property;
		// TODO [#2] Gray out the button when value is unchanged
		// TODO [#2] Add reset to default button
		IComponent undoButton = new BetterButton.Icon(screen, 48, 0, new TranslatableComponent(GuiTexts.UNDO_TOOLTIP_KEY), new TranslatableComponent(GuiTexts.UNDO_TOOLTIP_KEY))
				.addOnPressed(property::undoChanges)
				.addClass("better:undo");
		IComponent title = new Text(() -> Arrays.asList(this.identifier.getDisplayName()), Alignment.CENTER, Alignment.MIN);
		this.children.addAll(Arrays.asList(title, content, undoButton));
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
		this.filteredOut = !filter.matches(this.identifier);
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
		this.extraInfo.clear();
		this.extraInfo.add(new TextComponent(this.identifier.getName()).withStyle(ChatFormatting.YELLOW));
		this.extraInfo.addAll(this.identifier.getDisplayComment());
		this.extraInfo.add((new TranslatableComponent(GuiTexts.DEFAULT_VALUE_KEY, new TextComponent(Objects.toString(this.property.getSpec().getDefaultValue())))).withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public void invalidate()
	{
		this.children.forEach(IComponent::invalidate);
	}
}
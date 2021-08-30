package fr.max2.betterconfig.client.gui.better.widget;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.TextField;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for string properties */
public class StringInputField extends TextField
{
	/** The property to edit */
	private final IConfigPrimitive<String> property;
	private final IListener<String> propertyListener;
	
	private StringInputField(IComponentParent layoutManager, Font fontRenderer, IConfigPrimitive<String> property, Component title)
	{
		super(layoutManager, fontRenderer, title);
		this.property = property;
		this.setValue(property.getValue());
		this.setResponder(this::updateTextColor);
		
		this.propertyListener = this::setValue;
		this.property.onChanged(this.propertyListener);
		
		this.config.sizeOverride = new Size(VALUE_WIDTH - 2, VALUE_HEIGHT - 2);
	}
	
	/** Updates the color of the text to indicates an error */
	private void updateTextColor(String text)
	{
		this.setTextColor(this.property.getSpec().isAllowed(text) ? DEFAULT_FIELD_TEXT_COLOR : ERROR_FIELD_TEXT_COLOR);
	}
	
	@Override
	protected void onValidate(String text)
	{
		if (this.property.getSpec().isAllowed(text))
		{
			this.property.setValue(text);
		}
	}
	
	@Override
	public void invalidate()
	{
		this.property.removeOnChangedListener(this.propertyListener);
	}

	/** Creates a widget for string values */
	public static StringInputField stringOption(BetterConfigScreen screen, IComponentParent layoutManager, IConfigPrimitive<String> property)
	{
		return new StringInputField(layoutManager, screen.getFont(), property, property.getDisplayName());
	}
}

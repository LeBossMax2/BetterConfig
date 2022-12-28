package fr.max2.betterconfig.client.gui.better.widget;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.better.Constants;
import fr.max2.betterconfig.client.gui.component.widget.TextField;
import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/** The widget for string properties */
public class StringInputField extends TextField
{
	/** The property to edit */
	private final ConfigPrimitive<String> property;
	private final IListener<String> propertyListener;
	
	private StringInputField(Font fontRenderer, ConfigPrimitive<String> property, Component title)
	{
		super(fontRenderer, title);
		this.addClass("better:string_input");
		this.property = property;
		this.setValue(property.getValue());
		this.setResponder(this::updateTextColor);
		
		this.propertyListener = this::setValue;
		this.property.onChanged(this.propertyListener);
	}
	
	/** Updates the color of the text to indicates an error */
	private void updateTextColor(String text)
	{
		this.setTextColor(this.property.getSpec().node().isAllowed(text) ? Constants.DEFAULT_FIELD_TEXT_COLOR : Constants.ERROR_FIELD_TEXT_COLOR);
	}
	
	@Override
	protected void onValidate(String text)
	{
		if (this.property.getSpec().node().isAllowed(text))
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
	public static StringInputField stringOption(BetterConfigScreen screen, IConfigName identifier, ConfigPrimitive<String> property)
	{
		return new StringInputField(screen.getFont(), property, identifier.getDisplayName());
	}
}

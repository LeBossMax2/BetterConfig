package fr.max2.betterconfig.client.gui.better.widget;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.NumberField;
import fr.max2.betterconfig.client.util.INumberType;
import fr.max2.betterconfig.client.util.NumberTypes;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for number properties */
public class NumberInputField<N extends Number> extends NumberField<N> implements IBetterElement
{
	/** The property to edit */
	private final IConfigPrimitive<N> property;
	private final IListener<N> propertyListener;

	public NumberInputField(FontRenderer fontRenderer, int x, INumberType<N> numberType, IConfigPrimitive<N> property, ITextComponent title)
	{
		super(fontRenderer, x, 0, VALUE_WIDTH, VALUE_HEIGHT, title, numberType, property.getValue());
		this.property = property;
		this.inputField.setResponder(this::updateTextColor);
		
		this.propertyListener = this::setValue;
		this.property.onChanged(this.propertyListener);
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
	
	@Override
	public void invalidate()
	{
		this.property.removeOnChangedListener(this.propertyListener);
	}

	/** Creates a widget for number values */
	@SuppressWarnings("unchecked")
	public static <N extends Number> NumberInputField<N> numberOption(BetterConfigScreen screen, int xPos, IConfigPrimitive<N> property)
	{
		return new NumberInputField<>(screen.getFont(), xPos, NumberTypes.getType((Class<N>)property.getSpec().getValueClass()), property, property.getDisplayName());
	}
}

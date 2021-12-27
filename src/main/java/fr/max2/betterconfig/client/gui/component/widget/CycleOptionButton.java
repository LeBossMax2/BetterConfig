package fr.max2.betterconfig.client.gui.component.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.client.gui.components.Button.OnTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * A button for cycling between several options
 * @param <V> the type of option value
 */
public class CycleOptionButton<V> extends Button
{
	/** The translation key of the text to show when no option is selected */
	public static final String NO_OPTION_KEY = BetterConfig.MODID + ".option.no_value";
	/** The translation key of the text to show when the selected option is true */
	public static final String TRUE_OPTION_KEY = BetterConfig.MODID + ".option.true";
	/** The translation key of the text to show when the selected option is false */
	public static final String FALSE_OPTION_KEY = BetterConfig.MODID + ".option.false";
	/** The list of available option values */
	private final List<? extends V> acceptedValues;
	/** The function to get the text to show from the selection option */
	private final Function<? super V, Component> valueToText;
	/** The index of the selected option in the list */
	private int index;

	@SuppressWarnings("unchecked")
	public CycleOptionButton(List<? extends V> acceptedValues, Function<? super V, Component> valueToText, V currentValue, Consumer<CycleOptionButton<V>> handler, OnTooltip tooltip)
	{
		super(getValueText(valueToText, currentValue), thiz ->
		{
			CycleOptionButton<V> thisButton = ((CycleOptionButton<V>)thiz); 
			thisButton.cycleOption();
			handler.accept(thisButton);
		}, tooltip);
		this.acceptedValues = acceptedValues;
		this.valueToText = valueToText;
		this.index = acceptedValues.indexOf(currentValue);
	}
	
	/** Gets the currently selected option value */
	public V getCurrentValue()
	{
		return this.acceptedValues.size() == 0 ? null : this.acceptedValues.get(this.index % this.acceptedValues.size());
	}
	
	protected void setCurrentValue(V newValue)
	{
		this.index = this.acceptedValues.indexOf(newValue);
		this.widget.setMessage(getValueText(this.valueToText, newValue));
	}
	
	/** Selects the next available option */
	public void cycleOption()
	{
		this.index++;
		if (this.index >= this.acceptedValues.size())
		{
			this.index = 0;
		}
		this.widget.setMessage(getValueText(this.valueToText, this.getCurrentValue()));
	}
	
	/** Gets the text corresponding to the given option value using the given translation function */
	private static <V> Component getValueText(Function<? super V, Component> valueToText, V value)
	{
		return value == null ? new TranslatableComponent(NO_OPTION_KEY) : valueToText.apply(value);
	}
	
}

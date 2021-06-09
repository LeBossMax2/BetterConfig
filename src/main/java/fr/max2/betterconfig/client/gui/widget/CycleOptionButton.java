package fr.max2.betterconfig.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CycleOptionButton<V> extends Button
{
	public static final String NO_OPTION_KEY = BetterConfig.MODID + ".option.no_value";
	public static final String TRUE_OPTION_KEY = BetterConfig.MODID + ".option.true";
	public static final String FALSE_OPTION_KEY = BetterConfig.MODID + ".option.false";
	private final List<? extends V> acceptedValues;
	private final Function<? super V, ITextComponent> valueToText;
	private int index;

	@SuppressWarnings("unchecked")
	public CycleOptionButton(int xPos, int yPos, int width, int height, List<? extends V> acceptedValues, Function<? super V, ITextComponent> valueToText, V currentValue, Consumer<CycleOptionButton<V>> handler, ITooltip tooltip)
	{
		super(xPos, yPos, width, height, getValueText(valueToText, currentValue), thiz ->
		{
			CycleOptionButton<V> thisButton = ((CycleOptionButton<V>)thiz); 
			thisButton.cycleOption();
			handler.accept(thisButton);
		}, tooltip);
		this.acceptedValues = acceptedValues;
		this.valueToText = valueToText;
		this.index = acceptedValues.indexOf(currentValue);
	}
	
	public V getCurrentValue()
	{
		return this.acceptedValues.size() == 0 ? null : this.acceptedValues.get(this.index % this.acceptedValues.size());
	}
	
	public void cycleOption()
	{
		this.index++;
		if (this.index >= this.acceptedValues.size())
		{
			this.index = 0;
		}
		this.setMessage(getValueText(this.valueToText, this.getCurrentValue()));
	}
	
	private static <V> ITextComponent getValueText(Function<? super V, ITextComponent> valueToText, V value)
	{
		return value == null ? new TranslationTextComponent(NO_OPTION_KEY) : valueToText.apply(value);
	}
	
}

package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.CycleOptionButton;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for option buttons */
public class OptionButton<V> extends CycleOptionButton<V> implements IBetterElement
{
	private final IConfigPrimitive<V> property;
	private final IListener<V> propertyListener;
	
	private OptionButton(int xPos, List<? extends V> acceptedValues,
		Function<? super V, ITextComponent> valueToText, IConfigPrimitive<V> property)
	{
		super(xPos, 0, VALUE_WIDTH, VALUE_HEIGHT,
			acceptedValues.stream().filter(property.getSpec()::isAllowed).collect(Collectors.toList()),
			valueToText,
			property.getValue(), thiz -> property.setValue(thiz.getCurrentValue()),
			NO_TOOLTIP);
		
		this.property = property;
		this.propertyListener = this::setCurrentValue;
		this.property.onChanged(this.propertyListener);
	}

	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		this.setY(y);
		return this.height;
	}
	
	/** Creates a widget for boolean values */
	public static OptionButton<Boolean> booleanOption(int xPos, IConfigPrimitive<Boolean> property)
	{
		return new OptionButton<>(
			xPos,
			Arrays.asList(false, true),
			bool -> new TranslationTextComponent(bool ? TRUE_OPTION_KEY : FALSE_OPTION_KEY),
			property);
	}

	/** Creates a widget for enum values */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> OptionButton<E> enumOption(int xPos, IConfigPrimitive<E> property)
	{
		return new OptionButton<>(
			xPos,
			Arrays.asList(((Class<E>)property.getSpec().getValueClass()).getEnumConstants()),
			enuw -> new StringTextComponent(enuw.name()),
			property);
	}
	
	@Override
	public void invalidate()
	{
		this.property.removeOnChangedListener(this.propertyListener);
	}
}

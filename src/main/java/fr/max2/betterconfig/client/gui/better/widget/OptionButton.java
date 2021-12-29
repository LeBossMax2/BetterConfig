package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.component.widget.CycleOptionButton;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for option buttons */
public class OptionButton<V> extends CycleOptionButton<V> 
{
	public static final StyleRule STYLE = StyleRule.when().hasClass("better:option_button").then()
			.set(ComponentLayoutConfig.SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
			.build();
	
	private final IConfigPrimitive<V> property;
	private final IListener<V> propertyListener;
	
	private OptionButton(List<? extends V> acceptedValues,
		Function<? super V, Component> valueToText, IConfigPrimitive<V> property)
	{
		super(
			acceptedValues.stream().filter(property.getSpec()::isAllowed).collect(Collectors.toList()),
			valueToText,
			property.getValue(), thiz -> property.setValue(thiz.getCurrentValue()),
			NO_OVERLAY);
		this.addClass("better:option_button");
		
		this.property = property;
		this.propertyListener = this::setCurrentValue;
		this.property.onChanged(this.propertyListener);
	}
	
	/** Creates a widget for boolean values */
	public static OptionButton<Boolean> booleanOption(IConfigPrimitive<Boolean> property)
	{
		return new OptionButton<>(
			Arrays.asList(false, true),
			bool -> new TranslatableComponent(bool ? TRUE_OPTION_KEY : FALSE_OPTION_KEY),
			property);
	}

	/** Creates a widget for enum values */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> OptionButton<E> enumOption(IConfigPrimitive<E> property)
	{
		return new OptionButton<>(
			Arrays.asList(((Class<E>)property.getSpec().getValueClass()).getEnumConstants()),
			enuw -> new TextComponent(enuw.name()),
			property);
	}
	
	@Override
	public void invalidate()
	{
		this.property.removeOnChangedListener(this.propertyListener);
	}
}

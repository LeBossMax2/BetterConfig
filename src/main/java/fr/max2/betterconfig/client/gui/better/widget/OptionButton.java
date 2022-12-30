package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.component.widget.CycleOptionButton;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.Component;

/** The widget for option buttons */
public class OptionButton<V> extends CycleOptionButton<V>
{
	private final ConfigPrimitive<V> property;
	private final IListener<V> propertyListener;

	private OptionButton(List<? extends V> acceptedValues,
		Function<? super V, Component> valueToText, ConfigPrimitive<V> property)
	{
		super(
			acceptedValues.stream().filter(property.getSpec()::isAllowed).collect(Collectors.toList()),
			valueToText,
			property.getValue(),
			NO_OVERLAY);
		this.addOnPressed(() -> property.setValue(this.getCurrentValue()));
		this.addClass("better:option_button");

		this.property = property;
		this.propertyListener = this::setCurrentValue;
		this.property.onChanged().add(this.propertyListener);
	}

	/** Creates a widget for boolean values */
	public static OptionButton<Boolean> booleanOption(ConfigPrimitive<Boolean> property)
	{
		return new OptionButton<>(
			Arrays.asList(false, true),
			bool -> Component.translatable(bool ? TRUE_OPTION_KEY : FALSE_OPTION_KEY),
			property);
	}

	/** Creates a widget for enum values */
	public static <E extends Enum<E>> OptionButton<E> enumOption(ConfigPrimitive<E> property)
	{
		return new OptionButton<>(
			Arrays.asList(property.getSpec().valueClass().getEnumConstants()),
			enuw -> Component.literal(enuw.name()),
			property);
	}

	@Override
	public void invalidate()
	{
		this.property.onChanged().remove(this.propertyListener);
	}
}

package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.CycleOptionButton;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for option buttons */
public class OptionButton<V> extends CycleOptionButton<V> 
{
	private final IConfigPrimitive<V> property;
	private final IListener<V> propertyListener;
	
	private OptionButton(IComponentParent layoutManager, List<? extends V> acceptedValues,
		Function<? super V, Component> valueToText, IConfigPrimitive<V> property)
	{
		super(layoutManager,
			acceptedValues.stream().filter(property.getSpec()::isAllowed).collect(Collectors.toList()),
			valueToText,
			property.getValue(), thiz -> property.setValue(thiz.getCurrentValue()),
			NO_TOOLTIP);
		
		this.property = property;
		this.propertyListener = this::setCurrentValue;
		this.property.onChanged(this.propertyListener);
		this.config.sizeOverride = new Size(VALUE_WIDTH, VALUE_HEIGHT);
	}
	
	/** Creates a widget for boolean values */
	public static OptionButton<Boolean> booleanOption(IComponentParent layoutManager, IConfigPrimitive<Boolean> property)
	{
		return new OptionButton<>(
			layoutManager,
			Arrays.asList(false, true),
			bool -> new TranslatableComponent(bool ? TRUE_OPTION_KEY : FALSE_OPTION_KEY),
			property);
	}

	/** Creates a widget for enum values */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> OptionButton<E> enumOption(IComponentParent layoutManager, IConfigPrimitive<E> property)
	{
		return new OptionButton<>(
			layoutManager,
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

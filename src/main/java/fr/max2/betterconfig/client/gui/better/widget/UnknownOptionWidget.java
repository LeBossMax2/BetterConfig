package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Objects;

import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.TextComponent;

/** The widget for properties of unknown type */
public class UnknownOptionWidget extends Button
{
	private final IConfigPrimitive<?> property;
	private final IListener<Object> propertyListener;
	
	public UnknownOptionWidget(IConfigPrimitive<?> property)
	{
		super(new TextComponent(Objects.toString(property.getValue())), thiz -> {}, NO_OVERLAY);
		this.addClass("better:unknown");
		this.widget.active = false;
		
		this.property = property;
		this.propertyListener = newVal -> this.setMessage(new TextComponent(Objects.toString(newVal)));
		this.property.onChanged(this.propertyListener);
	}
	
	@Override
	public void invalidate()
	{
		this.property.removeOnChangedListener(this.propertyListener);
	}
}

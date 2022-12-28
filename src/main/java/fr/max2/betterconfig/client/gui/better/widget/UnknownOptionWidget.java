package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Objects;

import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.TextComponent;

/** The widget for properties of unknown type */
public class UnknownOptionWidget extends Button
{
	private final ConfigPrimitive<?> property;
	private final IListener<Object> propertyListener;
	
	public UnknownOptionWidget(ConfigPrimitive<?> property)
	{
		super(new TextComponent(Objects.toString(property.getValue())), NO_OVERLAY);
		this.addClass("better:unknown");
		this.setActive(false);
		
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

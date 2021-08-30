package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Objects;

import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.TextComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for properties of unknown type */
public class UnknownOptionWidget extends Button
{
	private final IConfigPrimitive<?> property;
	private final IListener<Object> propertyListener;
	
	public UnknownOptionWidget(IComponentParent layoutManager, IConfigPrimitive<?> property)
	{
		super(layoutManager, new TextComponent(Objects.toString(property.getValue())), thiz -> {}, NO_TOOLTIP);
		this.widget.active = false;
		
		this.property = property;
		this.propertyListener = newVal -> this.setMessage(new TextComponent(Objects.toString(newVal)));
		this.property.onChanged(this.propertyListener);
		this.config.sizeOverride = new Size(VALUE_WIDTH, VALUE_HEIGHT);
	}
	
	@Override
	public void invalidate()
	{
		this.property.removeOnChangedListener(this.propertyListener);
	}
}

package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Objects;

import javax.annotation.Nullable;

import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigUnknown;
import fr.max2.betterconfig.util.IEvent;
import net.minecraft.network.chat.Component;

/** The widget for properties of unknown type */
public class UnknownOptionWidget extends Button
{
	private final IEvent.Guard propertyGuard;

	public UnknownOptionWidget(Component displayString, @Nullable ConfigPrimitive<?> property)
	{
		super(displayString, NO_OVERLAY);
		this.addClass("better:unknown");
		this.setActive(false);

		this.propertyGuard = property == null ? null : property.onChanged().add(this::setValue);
	}

	public UnknownOptionWidget(ConfigPrimitive<?> property)
	{
		this(Component.literal(Objects.toString(property.getValue())), property);
	}

	public UnknownOptionWidget(ConfigUnknown property)
	{
		this(Component.literal(Objects.toString(property.getValue())), null);
	}
	
	protected void setValue(Object newVal)
	{
		this.setMessage(Component.literal(Objects.toString(newVal)));
	}

	@Override
	public void invalidate()
	{
		if (this.propertyGuard != null)
			this.propertyGuard.close();
	}
}

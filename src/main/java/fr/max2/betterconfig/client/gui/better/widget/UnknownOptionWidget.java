package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Objects;

import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.Button;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import net.minecraft.util.text.StringTextComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The widget for properties of unknown type */
public class UnknownOptionWidget extends Button implements IBetterElement
{
	public UnknownOptionWidget(int xPos, IConfigPrimitive<?> property)
	{
		super(xPos, 0, VALUE_WIDTH, VALUE_HEIGHT, new StringTextComponent(Objects.toString(property.getValue())), thiz -> {}, NO_TOOLTIP);
		this.active = false;
	}

	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		this.y = y;
		return VALUE_HEIGHT;
	}
}

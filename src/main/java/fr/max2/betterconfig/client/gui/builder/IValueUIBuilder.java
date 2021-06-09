package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

import fr.max2.betterconfig.ConfigProperty;

public interface IValueUIBuilder<P>
{
	P buildBoolean(ConfigProperty<Boolean> property);
	P buildNumber(ConfigProperty<? extends Number> property);
	P buildString(ConfigProperty<String> property);
	<E extends Enum<E>> P buildEnum(ConfigProperty<E> property);
	P buildList(ConfigProperty<? extends List<?>> property);
	
	P buildUnknown(ConfigProperty<?> property);
}

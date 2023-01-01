package fr.max2.betterconfig.client.gui.style;

import fr.max2.betterconfig.client.gui.style.operator.IStyleOperation;

public record StyleValue<T>
(
	StyleProperty<T> property,
	IStyleOperation<T> propertyEffect
)
{
	@Override
	public String toString()
	{
		return this.property.toString() + this.propertyEffect.toString();
	}
}

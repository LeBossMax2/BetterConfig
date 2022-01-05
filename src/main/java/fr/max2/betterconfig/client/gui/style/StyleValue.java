package fr.max2.betterconfig.client.gui.style;

import fr.max2.betterconfig.client.gui.style.operator.IStyleOperation;

public class StyleValue<T>
{
	private final StyleProperty<T> property;
	private final IStyleOperation<T> propertyEffect;
	
	public StyleValue(StyleProperty<T> property, IStyleOperation<T> propertyEffect)
	{
		this.property = property;
		this.propertyEffect = propertyEffect;
	}
	
	public StyleProperty<T> getProperty()
	{
		return this.property;
	}
	
	public IStyleOperation<T> getPropertyEffect()
	{
		return this.propertyEffect;
	}
	
	@Override
	public String toString()
	{
		return this.property.toString() + this.propertyEffect.toString();
	}
}

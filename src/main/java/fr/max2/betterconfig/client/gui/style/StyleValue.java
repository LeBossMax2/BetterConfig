package fr.max2.betterconfig.client.gui.style;

public class StyleValue<T>
{
	private final StyleProperty<T> property;
	private final IStyleEffect<T> propertyEffect;
	
	public StyleValue(StyleProperty<T> property, IStyleEffect<T> propertyEffect)
	{
		this.property = property;
		this.propertyEffect = propertyEffect;
	}
	
	public StyleProperty<T> getProperty()
	{
		return this.property;
	}
	
	public IStyleEffect<T> getPropertyEffect()
	{
		return this.propertyEffect;
	}
	
	@Override
	public String toString()
	{
		return this.property.toString() + this.propertyEffect.toString();
	}
}

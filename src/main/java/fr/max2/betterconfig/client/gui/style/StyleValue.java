package fr.max2.betterconfig.client.gui.style;

public class StyleValue<T>
{
	private final StyleProperty<T> property;
	private final T propertyValue;
	
	public StyleValue(StyleProperty<T> property, T propertyValue)
	{
		this.property = property;
		this.propertyValue = propertyValue;
	}
	
	public StyleProperty<T> getProperty()
	{
		return this.property;
	}
	
	public T getPropertyValue()
	{
		return this.propertyValue;
	}
	
	@Override
	public String toString()
	{
		return this.property + " = " + this.propertyValue;
	}
}

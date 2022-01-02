package fr.max2.betterconfig.client.gui.style;

import fr.max2.betterconfig.client.gui.component.IComponent;

public class ProcessedStyleRule<T>
{
	private ISelector condition;
	private T propertyValue;
	
	public ProcessedStyleRule(ISelector condition, StyleValue<T> value)
	{
		this.condition = condition;
		this.propertyValue = value.getPropertyValue();
	}

	public boolean matches(IComponent component)
	{
		return this.condition.test(component);
	}
	
	public T getPropertyValue()
	{
		return this.propertyValue;
	}
	
	@Override
	public String toString()
	{
		return this.condition + " => " + this.propertyValue;
	}
}

package fr.max2.betterconfig.client.gui.style;

import java.util.List;

public class ProcessedStyleRule<T>
{
	private List<IComponentSelector> conditions;
	private T propertyValue;
	
	public ProcessedStyleRule(List<IComponentSelector> conditions, StyleValue<T> value)
	{
		this.conditions = conditions;
		this.propertyValue = value.getPropertyValue();
	}

	public boolean matches(IStylableComponent component)
	{
		for (IComponentSelector condition : this.conditions)
		{
			if (!condition.test(component))
				return false;
		}
		return true;
	}
	
	public T getPropertyValue()
	{
		return this.propertyValue;
	}
}

package fr.max2.betterconfig.client.gui.style;

import java.util.List;

public class ProcessedStyleRule<T>
{
	private List<IComponentSelector> conditions;
	private StyleProperty<T> property;
	private T propertyValue;
	
	public ProcessedStyleRule(List<IComponentSelector> conditions, StyleValue<T> value)
	{
		this.conditions = conditions;
		this.property = value.getProperty();
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
		return propertyValue;
	}
}

package fr.max2.betterconfig.client.gui.style;

import java.util.List;

import fr.max2.betterconfig.client.gui.component.IComponent;

public class ProcessedStyleRule<T>
{
	private List<IComponentSelector> conditions;
	private T propertyValue;
	
	public ProcessedStyleRule(List<IComponentSelector> conditions, StyleValue<T> value)
	{
		this.conditions = conditions;
		this.propertyValue = value.getPropertyValue();
	}

	public boolean matches(IComponent component)
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
	
	@Override
	public String toString()
	{
		return this.conditions + " => " + this.propertyValue;
	}
}

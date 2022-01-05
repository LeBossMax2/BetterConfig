package fr.max2.betterconfig.client.gui.style;

import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.style.operator.IStyleOperation;

public class ProcessedStyleRule<T>
{
	private ISelector condition;
	private IStyleOperation<T> propertyEffect;
	
	public ProcessedStyleRule(ISelector condition, StyleValue<T> value)
	{
		this.condition = condition;
		this.propertyEffect = value.getPropertyEffect();
	}

	public boolean matches(IComponent component)
	{
		return this.condition.test(component);
	}
	
	public IStyleOperation<T> getPropertyEffect()
	{
		return this.propertyEffect;
	}
	
	@Override
	public String toString()
	{
		return this.condition + " => " + this.propertyEffect;
	}
}

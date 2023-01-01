package fr.max2.betterconfig.client.gui.style;

import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.style.operator.IStyleOperation;

public record ProcessedStyleRule<T>
(
	ISelector condition,
	IStyleOperation<T> propertyEffect
)
{
	public ProcessedStyleRule(ISelector condition, StyleValue<T> value)
	{
		this(condition, value.propertyEffect());
	}

	public boolean matches(IComponent component)
	{
		return this.condition.test(component);
	}
	
	@Override
	public String toString()
	{
		return this.condition + " => " + this.propertyEffect;
	}
}

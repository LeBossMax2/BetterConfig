package fr.max2.betterconfig.client.gui.style;

import java.util.function.Predicate;

public class StyleCondition<T>
{
	private PropertyIdentifier<T> property;
	private Predicate<T> valuePredicate;
	
	public StyleCondition(PropertyIdentifier<T> property, Predicate<T> valuePredicate)
	{
		this.property = property;
		this.valuePredicate = valuePredicate;
	}

	public boolean matches(IStylableComponent component)
	{
		return this.valuePredicate.test(component.getProperty(this.property));
	}
}

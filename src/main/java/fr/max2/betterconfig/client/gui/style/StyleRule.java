package fr.max2.betterconfig.client.gui.style;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

public class StyleRule
{
	private final List<StyleCondition<?>> conditions;
	private final List<StyleValue<?>> values;
	
	public StyleRule(List<StyleCondition<?>> conditions, List<StyleValue<?>> values)
	{
		this.conditions = conditions;
		this.values = values;
	}

	public List<StyleCondition<?>> getConditions()
	{
		return conditions;
	}
	
	public List<StyleValue<?>> getValues()
	{
		return values;
	}
	
	public static ConditionBuilder when()
	{
		return new ConditionBuilder();
	}
	
	public static class ConditionBuilder
	{
		private final List<StyleCondition<?>> conditions = new ArrayList<>();
		
		private ConditionBuilder()
		{ }
		
		public <T> ConditionBuilder condition(PropertyIdentifier<T> property, Predicate<T> valuePredicate)
		{
			this.conditions.add(new StyleCondition<>(property, valuePredicate));
			return this;
		}
		
		public <T> ConditionBuilder equals(PropertyIdentifier<T> property, T value)
		{
			return this.condition(property, val -> val.equals(value));
		}
		
		public <T> ConditionBuilder contains(PropertyIdentifier<List<T>> property, T value)
		{
			return this.condition(property, val -> val.contains(value));
		}
		
		public ValueBuilder then()
		{
			return new ValueBuilder(this);
		}
	}
	
	public static class ValueBuilder
	{
		private final ConditionBuilder parent;
		private final List<StyleValue<?>> values = new ArrayList<>();
		
		private ValueBuilder(ConditionBuilder parent)
		{
			this.parent = parent;
		}
		
		public <T> ValueBuilder set(StyleProperty<T> property, T propertyValue)
		{
			this.values.add(new StyleValue<>(property, propertyValue));
			return this;
		}
		
		public StyleRule build()
		{
			return new StyleRule(ImmutableList.copyOf(this.parent.conditions), ImmutableList.copyOf(this.values));
		}
	}
}

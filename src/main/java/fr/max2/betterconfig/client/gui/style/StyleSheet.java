package fr.max2.betterconfig.client.gui.style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleSheet
{
	private Map<StyleProperty<?>, List<ProcessedStyleRule<?>>> rules;
	
	public StyleSheet(List<StyleRule> rules)
	{
		this.rules = new HashMap<>();
		for (StyleRule rule : rules)
		{
			for (StyleValue<?> v : rule.getValues())
			{
				List<ProcessedStyleRule<?>> r = this.rules.get(v.getProperty());
				if (r == null)
				{
					r = new ArrayList<>();
					this.rules.put(v.getProperty(), r);
				}
				r.add(new ProcessedStyleRule<>(rule.getConditions(), v));
			}
		}
	}
	
	public StyleSheet(StyleRule... rules)
	{
		this(Arrays.asList(rules));
	}

	public <T> T computePropertyValue(IStylableComponent component, StyleProperty<T> property)
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ProcessedStyleRule<T>> rules = (List<ProcessedStyleRule<T>>)(List)this.rules.get(property);
		for (ProcessedStyleRule<T> rule : rules)
		{
			if (rule.matches(component))
				return rule.getPropertyValue();
		}
		return property.defaultValue;
	}
}

package fr.max2.betterconfig.client.gui.style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.IComponent;
import net.minecraft.resources.ResourceLocation;

public class StyleSheet
{
	public static final String STYLESHEET_DIR = "stylesheets";
	public static final ResourceLocation DEFAULT_STYLESHEET = new ResourceLocation(BetterConfig.MODID, "default_stylesheet");
	
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

	public <T> T computePropertyValue(IComponent component, StyleProperty<T> property)
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ProcessedStyleRule<T>> rules = (List)this.rules.get(property);
		if (rules == null)
			return property.defaultValue;
			
		for (ProcessedStyleRule<T> rule : rules)
		{
			if (rule.matches(component))
				return rule.getPropertyValue();
		}
		return property.defaultValue;
	}
	
	public static class Builder
	{
		private final List<StyleRule> rules;

		public Builder()
		{
			this.rules = new ArrayList<>();
		}

		public Builder(List<StyleRule> rules)
		{
			this.rules = rules;
		}
		
		public Builder(StyleRule... rules)
		{
			this(Arrays.asList(rules));
		}
		
		public StyleSheet build()
		{
			return new StyleSheet(this.rules);
		}
	}
	
}

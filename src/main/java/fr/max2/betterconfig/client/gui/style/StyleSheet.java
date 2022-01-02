package fr.max2.betterconfig.client.gui.style;

import java.io.IOException;
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
	
	private final StyleSheet parent;
	private final Map<StyleProperty<?>, List<ProcessedStyleRule<?>>> rules;
	
	private StyleSheet(StyleSheet parent, List<StyleRule> rules)
	{
		this.parent = parent;
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
				r.add(new ProcessedStyleRule<>(rule.getCondition(), v));
			}
		}
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
		
		if (this.parent != null)
			return this.parent.computePropertyValue(component, property);
		
		return property.defaultValue;
	}
	
	public static class Builder
	{
		private ResourceLocation parentSheet = null;
		private final List<StyleRule> rules;

		public Builder()
		{
			this.rules = new ArrayList<>();
		}

		public Builder add(List<StyleRule> rules)
		{
			this.rules.addAll(rules);
			return this;
		}
		
		public Builder add(StyleRule... rules)
		{
			return this.add(Arrays.asList(rules));
		}
		
		public Builder parentSheet(ResourceLocation parentSheet)
		{
			this.parentSheet = parentSheet;
			return this;
		}
		
		public StyleSheet build() throws IOException
		{
			StyleSheet parent = null;
			if (this.parentSheet != null)
				parent = StyleSheetManager.INSTANCE.getStyleSheet(this.parentSheet);
			return new StyleSheet(parent, this.rules);
		}
	}
	
}

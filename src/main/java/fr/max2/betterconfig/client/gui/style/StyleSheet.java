package fr.max2.betterconfig.client.gui.style;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
		
		for (List<?> ruleList : this.rules.values())
		{
			Collections.reverse(ruleList);
		}
	}
	
    public <I, T> T computePropertyValue(IComponent component, StyleProperty<T> property)
    {
            T res = this.computePropertyStram(property)
                            .filter(rule -> rule.matches(component))
                            .map(ProcessedStyleRule::getPropertyEffect)
                            .reduce(null, (val, effect) -> effect.updateValue(val, property.defaultValue), (a, b) ->
                            {
                            	throw new UnsupportedOperationException();
                            });
            return res == null ? property.defaultValue : res;
    }

    private <T> Stream<ProcessedStyleRule<T>> computePropertyStram(StyleProperty<T> property)
    {
            Stream<ProcessedStyleRule<T>> valueStream = Stream.empty();
            
            if (this.parent != null)
                valueStream = Stream.concat(valueStream, this.parent.computePropertyStram(property));
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<ProcessedStyleRule<T>> rules = (List)this.rules.get(property);
            if (rules != null)
                    valueStream = rules.stream();

            return valueStream;
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

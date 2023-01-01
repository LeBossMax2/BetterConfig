package fr.max2.betterconfig.client.gui.style;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.IComponent;
import net.minecraft.resources.ResourceLocation;

public class StyleSheet
{
	public static final String STYLESHEET_DIR = "stylesheets";
	public static final ResourceLocation DEFAULT_STYLESHEET = new ResourceLocation(BetterConfig.MODID, "default_stylesheet");

	@Nullable
	private final StyleSheet parent;
	private final Map<StyleProperty<?>, List<ProcessedStyleRule<?>>> rules;

	private StyleSheet(@Nullable StyleSheet parent, List<StyleRule> rules)
	{
		this.parent = parent;
		this.rules = new HashMap<>();

		for (StyleRule rule : rules)
		{
			for (StyleValue<?> styleValue : rule.values())
			{
				List<ProcessedStyleRule<?>> processedRules = this.rules.get(styleValue.property());
				if (processedRules == null)
				{
					processedRules = new ArrayList<>();
					this.rules.put(styleValue.property(), processedRules);
				}
				processedRules.add(new ProcessedStyleRule<>(rule.condition(), styleValue));
			}
		}

		for (List<?> ruleList : this.rules.values())
		{
			Collections.reverse(ruleList);
		}
	}

	public <T> T computePropertyValue(IComponent component, StyleProperty<T> property)
	{
		T res =
			this.computePropertyStream(property)
				.filter(rule -> rule.matches(component))
				.map(ProcessedStyleRule::propertyEffect)
				.reduce(
					null,
					(val, effect) -> effect.updateValue(val, property.defaultValue()),
					(a, b) ->
					{
						throw new UnsupportedOperationException();
					});
		return res == null ? property.defaultValue() : res;
	}

	private <T> Stream<ProcessedStyleRule<T>> computePropertyStream(StyleProperty<T> property)
	{
		Stream<ProcessedStyleRule<T>> valueStream = Stream.empty();

		@SuppressWarnings("unchecked")
		List<ProcessedStyleRule<T>> rules = (List<ProcessedStyleRule<T>>)(List<?>)this.rules.get(property);
		if (rules != null)
			valueStream = rules.stream();

		if (this.parent != null)
			valueStream = Stream.concat(valueStream, this.parent.computePropertyStream(property));

		return valueStream;
	}

	public static class Builder
	{
		private ResourceLocation parentSheet = null;
		private final List<StyleRule> rules = new ArrayList<>();

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
				parent = StyleSheetManager.INSTANCE.getStyleSheet(this.parentSheet).orElse(null);

			return new StyleSheet(parent, this.rules);
		}
	}

}

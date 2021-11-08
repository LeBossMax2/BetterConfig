package fr.max2.betterconfig.client.gui.style;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.style.StyleRule.Serializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;

public class StyleSheet
{
	public static Gson GSON = new GsonBuilder().registerTypeAdapter(StyleRule.class, Serializer.INSTANCE).registerTypeAdapter(Padding.class, Padding.Serializer.INSTANCE).create();
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

	public static StyleSheet findSheet(ResourceLocation sheetLocation) throws IOException
	{
        ResourceLocation resourceLocation = new ResourceLocation(sheetLocation.getNamespace(), STYLESHEET_DIR + "/" + sheetLocation.getPath() + ".json");
		Resource res = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
		try (InputStream is = res.getInputStream())
		{
			return GsonHelper.fromJson(GSON, new InputStreamReader(is, StandardCharsets.UTF_8), StyleSheet.Builder.class).build();
		}
	}
}

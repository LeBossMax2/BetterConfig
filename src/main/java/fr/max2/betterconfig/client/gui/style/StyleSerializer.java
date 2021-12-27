package fr.max2.betterconfig.client.gui.style;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;

public class StyleSerializer
{
	public static StyleSerializer INSTANCE = new StyleSerializer(Arrays.asList(
			Component.COMPONENT_TYPE,
			Component.COMPONENT_CLASSES,
			IBetterElement.FILTERED_OUT
		), Arrays.asList(
			ComponentLayoutConfig.SIZE_OVERRIDE,
			ComponentLayoutConfig.OUTER_PADDING,
			ComponentLayoutConfig.VISIBILITY,
			CompositeLayoutConfig.DIR,
			CompositeLayoutConfig.SPACING,
			CompositeLayoutConfig.INNER_PADDING,
			CompositeLayoutConfig.JUSTIFICATION,
			CompositeLayoutConfig.ALIGNMENT
		));
	
	public final Map<String, PropertyIdentifier<?>> componentProperties;
	public final Map<String, StyleProperty<?>> styleProperties;
	
	private StyleSerializer(List<PropertyIdentifier<?>> componentProperties, List<StyleProperty<?>> styleProperties)
	{
		//TODO immutable hashmap
		this.componentProperties = new HashMap<>();
		for (PropertyIdentifier<?> prop : componentProperties)
		{
			this.componentProperties.put(prop.name.toString(), prop);
		}

		this.styleProperties = new HashMap<>();
		for (StyleProperty<?> prop : styleProperties)
		{
			this.styleProperties.put(prop.name.toString(), prop);
		}
	}
	
	public PropertyIdentifier<?> getComponentProperty(String propertyIdentifier)
	{
		return this.componentProperties.get(propertyIdentifier);
	}
	
	public GsonBuilder registerSerializers(GsonBuilder gson)
	{
		return gson
				.registerTypeAdapter(StyleRule.class, new StyleRule.Serializer(this))
				.registerTypeAdapter(IComponentSelector.class, new IComponentSelector.Serializer(this));
	}
	
}

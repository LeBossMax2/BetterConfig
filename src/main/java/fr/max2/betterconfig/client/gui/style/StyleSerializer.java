package fr.max2.betterconfig.client.gui.style;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.client.gui.better.Foldout;
import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import net.minecraft.resources.ResourceLocation;

public class StyleSerializer
{
	public static StyleSerializer INSTANCE = new StyleSerializer(Arrays.asList(
			Component.COMPONENT_TYPE,
			Component.COMPONENT_CLASSES,
			Component.PARENT,
			Component.HOVERED,
			Component.FOCUSED,
			IBetterElement.FILTERED_OUT,
			Foldout.FOLDED
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
	
	private final Map<String, PropertyIdentifier<?>> componentProperties;
	private final Map<String, StyleProperty<?>> styleProperties;
	
	private StyleSerializer(List<PropertyIdentifier<?>> componentProperties, List<StyleProperty<?>> styleProperties)
	{
		this.componentProperties = componentProperties.stream().collect(ImmutableMap.toImmutableMap(prop -> prop.name.toString(), Function.identity()));
		this.styleProperties = styleProperties.stream().collect(ImmutableMap.toImmutableMap(prop -> prop.name.toString(), Function.identity()));
	}
	
	public PropertyIdentifier<?> getComponentProperty(String propertyIdentifier)
	{
		return this.componentProperties.get(propertyIdentifier);
	}
	
	public StyleProperty<?> getStyleProperty(String propertyIdentifier)
	{
		return this.styleProperties.get(propertyIdentifier);
	}
	
	public GsonBuilder registerSerializers(GsonBuilder gson)
	{
		return gson
				.registerTypeHierarchyAdapter(PropertyIdentifier.class, new PropertyIdentifier.Serializer(this))
				.registerTypeHierarchyAdapter(StyleProperty.class, new StyleProperty.Serializer(this))
				.registerTypeAdapter(StyleRule.class, new StyleRule.Serializer(this))
				.registerTypeAdapter(ISelector.class, ISelector.Serializer.INSTANCE)
				.registerTypeAdapter(IStyleEffect.class, IStyleEffect.Serializer.INSTANCE)
				.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
				.registerTypeAdapter(Padding.class, Padding.Serializer.INSTANCE);
	}
}

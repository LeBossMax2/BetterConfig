package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;

public class StyleRule
{
	private final List<IComponentSelector> conditions;
	private final List<StyleValue<?>> values;
	
	public StyleRule(List<IComponentSelector> conditions, List<StyleValue<?>> values)
	{
		this.conditions = conditions;
		this.values = values;
	}

	public List<IComponentSelector> getConditions()
	{
		return this.conditions;
	}
	
	public List<StyleValue<?>> getValues()
	{
		return this.values;
	}
	
	public static ConditionBuilder when()
	{
		return new ConditionBuilder();
	}
	
	public static class ConditionBuilder
	{
		private final List<IComponentSelector> conditions = new ArrayList<>();
		
		private ConditionBuilder()
		{ }
		
		public <T> ConditionBuilder condition(IComponentSelector selection)
		{
			this.conditions.add(selection);
			return this;
		}
		
		public <T> ConditionBuilder equals(PropertyIdentifier<T> property, T value)
		{
			return this.condition(new IComponentSelector.Equals<>(property, value));
		}
		
		public <T> ConditionBuilder contains(ListPropertyIdentifier<T> property, T value)
		{
			return this.condition(new IComponentSelector.Contains<>(property, value));
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
	
	public static class Serializer implements JsonSerializer<StyleRule>, JsonDeserializer<StyleRule>
	{
		public static Serializer INSTANCE = new Serializer(Arrays.asList(
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
		
		private final Map<String, PropertyIdentifier<?>> componentProperties;
		private final Map<String, StyleProperty<?>> styleProperties;
		
		
		private Serializer(List<PropertyIdentifier<?>> componentProperties, List<StyleProperty<?>> styleProperties)
		{
			this.componentProperties = new HashMap<>();
			this.styleProperties = new HashMap<>();
			for (PropertyIdentifier<?> prop : componentProperties)
			{
				this.componentProperties.put(prop.name.toString(), prop);
			}
			for (StyleProperty<?> prop : styleProperties)
			{
				this.styleProperties.put(prop.name.toString(), prop);
			}
		}

		@Override
		public JsonElement serialize(StyleRule src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			
			JsonArray conds = new JsonArray();
			for (IComponentSelector cond : src.conditions)
			{
				conds.add(cond.toJson(context));
			}
			obj.add("conditions", conds);

			JsonObject values = new JsonObject();
			for (StyleValue<?> val : src.values)
			{
				values.add(val.getProperty().name.toString(), context.serialize(val.getPropertyValue(), val.getProperty().type));
			}
			obj.add("values", values);
			
			return obj;
		}

		@Override
		public StyleRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();
			
			List<IComponentSelector> conditions = new ArrayList<>();
			for (JsonElement cond : obj.getAsJsonArray("conditions"))
			{
				conditions.add(deserializeSelector(cond, context));
			}
			
			List<StyleValue<?>> values = new ArrayList<>();			
			for (Entry<String, JsonElement> cond : obj.getAsJsonObject("values").entrySet())
			{
				StyleProperty<?> prop = this.styleProperties.get(cond.getKey());
				values.add(new StyleValue<>(prop, context.deserialize(cond.getValue(), prop.type)));
			}
			
			return new StyleRule(conditions, values);
		}
		
		private IComponentSelector deserializeSelector(JsonElement json, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();
			
			switch (obj.get("operator").getAsString())
			{
			case "equals":
				return IComponentSelector.Equals.fromJson(obj, context, this);
			case "contains":
				return IComponentSelector.Contains.fromJson(obj, context, this);
			default:
				return null;
			}
		}
		
		public PropertyIdentifier<?> getComponentProperty(String propertyIdentifier)
		{
			return this.componentProperties.get(propertyIdentifier);
		}
	}
}

package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

import fr.max2.betterconfig.client.gui.component.Component;

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
	
	public static ConditionsBuilder when()
	{
		return new ConditionsBuilder();
	}
	
	public static class ConditionsBuilder implements IConditionBuilder<ConditionsBuilder>
	{
		private final List<IComponentSelector> conditions = new ArrayList<>();
		
		private ConditionsBuilder()
		{ }
		
		public <T> ConditionsBuilder condition(IComponentSelector selection)
		{
			this.conditions.add(selection);
			return this;
		}
		
		public ValueBuilder then()
		{
			return new ValueBuilder(this);
		}
	}
	
	public static interface IConditionBuilder<Res>
	{
		<T> Res condition(IComponentSelector selection);
		
		default <T> Res equals(PropertyIdentifier<T> property, T value)
		{
			return this.condition(new IComponentSelector.Equals<>(property, value));
		}
		
		default <T> Res contains(ListPropertyIdentifier<T> property, T value)
		{
			return this.condition(new IComponentSelector.Contains<>(property, value));
		}
		
		default IConditionBuilder<Res> parent()
		{
			return new IConditionBuilder<Res>()
			{
				@Override
				public <T> Res condition(IComponentSelector selection)
				{
					return IConditionBuilder.this.condition(new IComponentSelector.Combinator(Component.PARENT, selection));
				}
			};
		}
	}
	
	public static class ValueBuilder
	{
		private final ConditionsBuilder parent;
		private final List<StyleValue<?>> values = new ArrayList<>();
		
		private ValueBuilder(ConditionsBuilder parent)
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
		private final StyleSerializer parent;
		
		public Serializer(StyleSerializer parent)
		{
			this.parent = parent;
		}

		@Override
		public JsonElement serialize(StyleRule src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			
			JsonArray conds = new JsonArray();
			for (IComponentSelector cond : src.conditions)
			{
				conds.add(context.serialize(cond, IComponentSelector.class));
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
				conditions.add(context.deserialize(cond, IComponentSelector.class));
			}
			
			List<StyleValue<?>> values = new ArrayList<>();			
			for (Entry<String, JsonElement> cond : obj.getAsJsonObject("values").entrySet())
			{
				StyleProperty<?> prop = this.parent.styleProperties.get(cond.getKey());
				values.add(new StyleValue<>(prop, context.deserialize(cond.getValue(), prop.type)));
			}
			
			return new StyleRule(conditions, values);
		}
	}
}

package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
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
	private final ISelector condition;
	private final List<StyleValue<?>> values;
	
	public StyleRule(ISelector condition, List<StyleValue<?>> values)
	{
		this.condition = condition;
		this.values = values;
	}

	public ISelector getCondition()
	{
		return this.condition;
	}
	
	public List<StyleValue<?>> getValues()
	{
		return this.values;
	}
	
	public static IConditionBuilder<StyleRuleBuilder> when()
	{
		return new StyleRuleBuilder().when();
	}
	
	public static class StyleRuleBuilder
	{
		private ISelector condition = null;
		
		private IConditionBuilder<StyleRuleBuilder> when()
		{
			return IConditionBuilder.build(selector ->
				{
					this.condition = selector;
					return this;
				});
		}
		
		public ValueBuilder then()
		{
			return new ValueBuilder(this);
		}
	}
	
	public static class ConditionsBuilder<Res> implements IConditionBuilder<ConditionsBuilder<Res>>
	{
		private final Function<List<ISelector>, Res> resultBuilder;
		private final List<ISelector> conditions = new ArrayList<>();
		
		private ConditionsBuilder(Function<List<ISelector>, Res> resultBuilder)
		{
			this.resultBuilder = resultBuilder;
		}
		
		@Override
		public <T> ConditionsBuilder<Res> condition(ISelector selection)
		{
			this.conditions.add(selection);
			return this;
		}
		
		public Res end()
		{
			return this.resultBuilder.apply(this.conditions);
		}
	}
	
	public static interface IConditionBuilder<Res>
	{
		<T> Res condition(ISelector selector);
		
		default IConditionBuilder<Res> not()
		{
			return build(selector -> this.condition(new ISelector.Not(selector)));
		}
		
		default ConditionsBuilder<Res> and()
		{
			return new ConditionsBuilder<>(conditions -> this.condition(new ISelector.And(conditions)));
		}
		
		default ConditionsBuilder<Res> or()
		{
			return new ConditionsBuilder<>(conditions -> this.condition(new ISelector.Or(conditions)));
		}
		
		default <T> Res equals(PropertyIdentifier<T> property, T value)
		{
			return this.condition(new ISelector.Equals<>(property, value));
		}
		
		default <T> Res contains(ListPropertyIdentifier<T> property, T value)
		{
			return this.condition(new ISelector.Contains<>(property, value));
		}
		
		default IConditionBuilder<Res> subProperty(PropertyIdentifier<? extends IPropertySource> property)
		{
			return build(selector -> this.condition(new ISelector.Combinator(property, selector)));
		}
		
		default Res is(PropertyIdentifier<Boolean> property)
		{
			return this.equals(property, true);
		}
		
		default IConditionBuilder<Res> parent()
		{
			return this.subProperty(Component.PARENT);
		}
		
		default Res type(String type)
		{
			return this.equals(Component.COMPONENT_TYPE, type);
		}
		
		default Res hasClass(String className)
		{
			return this.contains(Component.COMPONENT_CLASSES, className);
		}
		
		static <Res> IConditionBuilder<Res> build(Function<ISelector, Res> resultBuilder)
		{
			return new IConditionBuilder<Res>()
			{
				@Override
				public <T> Res condition(ISelector selector)
				{
					return resultBuilder.apply(selector);
				}
			};
		}
	}
	
	public static class ValueBuilder
	{
		private final StyleRuleBuilder parent;
		private final List<StyleValue<?>> values = new ArrayList<>();
		
		private ValueBuilder(StyleRuleBuilder parent)
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
			return new StyleRule(this.parent.condition, ImmutableList.copyOf(this.values));
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
			
			obj.add("condition", context.serialize(src.condition, ISelector.class));

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
			
			ISelector condition = context.deserialize(obj.get("condition"), ISelector.class);
			
			List<StyleValue<?>> values = new ArrayList<>();			
			for (Entry<String, JsonElement> cond : obj.getAsJsonObject("values").entrySet())
			{
				StyleProperty<?> prop = this.parent.getStyleProperty(cond.getKey());
				values.add(new StyleValue<>(prop, context.deserialize(cond.getValue(), prop.type)));
			}
			
			return new StyleRule(condition, values);
		}
	}
}

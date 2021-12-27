package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import fr.max2.betterconfig.client.gui.component.IComponent;

public interface IComponentSelector extends Predicate<IComponent>
{
	JsonElement toJson(JsonSerializationContext context);
	
	public static class Equals<T> implements IComponentSelector
	{
		private final PropertyIdentifier<T> property;
		private final T value;

		public Equals(PropertyIdentifier<T> property, T value)
		{
			this.property = property;
			this.value = value;
		}

		@Override
		public boolean test(IComponent component)
		{
			return this.value.equals(component.getProperty(this.property));
		}

		@Override
		public JsonElement toJson(JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("operator", "equals");
			obj.addProperty("property", this.property.name.toString());
			obj.add("value", context.serialize(this.value, this.property.type));
			return obj;
		}
		
		public static Equals<?> fromJson(JsonObject json, JsonDeserializationContext context, StyleSerializer sr)
		{
			PropertyIdentifier<?> property = sr.getComponentProperty(json.get("property").getAsString());
			return new Equals<>(property, context.deserialize(json.get("value"), property.type));
		}
	}
	
	public static class Contains<T> implements IComponentSelector
	{
		private final ListPropertyIdentifier<T> property;
		private final T value;

		public Contains(ListPropertyIdentifier<T> property, T value)
		{
			this.property = property;
			this.value = value;
		}

		@Override
		public boolean test(IComponent component)
		{
			return component.getProperty(this.property).contains(this.value);
		}

		@Override
		public JsonElement toJson(JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("operator", "contains");
			obj.addProperty("property", this.property.name.toString());
			obj.add("value", context.serialize(this.value, this.property.contentType));
			return obj;
		}
		
		public static Contains<?> fromJson(JsonObject json, JsonDeserializationContext context, StyleSerializer sr)
		{
			ListPropertyIdentifier<?> property = (ListPropertyIdentifier<?>)sr.getComponentProperty(json.get("property").getAsString());
			return new Contains<>(property, context.deserialize(json.get("value"), property.contentType));
		}
	}
	
	public static class Combinator implements IComponentSelector
	{
		private final PropertyIdentifier<? extends IComponent> property;
		private final IComponentSelector subSelector;

		public Combinator(PropertyIdentifier<? extends IComponent> property, IComponentSelector subSelector)
		{
			this.property = property;
			this.subSelector = subSelector;
		}

		@Override
		public boolean test(IComponent component)
		{
			return this.subSelector.test(component.getProperty(this.property));
		}

		@Override
		public JsonElement toJson(JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("operator", "combonator");
			obj.addProperty("property", this.property.name.toString());
			obj.add("sub_selector", context.serialize(this.subSelector, IComponentSelector.class));
			return obj;
		}
		
		@SuppressWarnings("unchecked")
		public static Combinator fromJson(JsonObject json, JsonDeserializationContext context, StyleSerializer sr)
		{
			PropertyIdentifier<? extends IComponent> property = (PropertyIdentifier<? extends IComponent>)sr.getComponentProperty(json.get("property").getAsString());
			IComponentSelector subSelector = context.deserialize(json.get("sub_selector"), IComponentSelector.class);
			return new Combinator(property, subSelector);
		}
	}
	
	public static class Serializer implements JsonSerializer<IComponentSelector>, JsonDeserializer<IComponentSelector>
	{
		private final StyleSerializer parent;
		
		public Serializer(StyleSerializer parent)
		{
			this.parent = parent;
		}
		
		@Override
		public JsonElement serialize(IComponentSelector src, Type typeOfSrc, JsonSerializationContext context)
		{
			return src.toJson(context);
		}
		
		@Override
		public IComponentSelector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		        throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();
			
			switch (obj.get("operator").getAsString())
			{
			case "equals":
				return IComponentSelector.Equals.fromJson(obj, context, this.parent);
			case "contains":
				return IComponentSelector.Contains.fromJson(obj, context, this.parent);
			case "combonator":
				return IComponentSelector.Combinator.fromJson(obj, context, this.parent);
			default:
				return null;
			}
		}
	}
}

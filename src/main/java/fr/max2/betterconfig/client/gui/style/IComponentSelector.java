package fr.max2.betterconfig.client.gui.style;

import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface IComponentSelector extends Predicate<IStylableComponent>
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
		public boolean test(IStylableComponent component)
		{
			return this.value.equals(component.getProperty(this.property));
		}

		@Override
		public JsonElement toJson(JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("property", this.property.name.toString());
			obj.addProperty("operator", "equals");
			obj.add("value", context.serialize(this.value, this.property.type));
			return obj;
		}
		
		public static Equals<?> fromJson(JsonObject json, JsonDeserializationContext context, StyleRule.Serializer sr)
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
		public boolean test(IStylableComponent component)
		{
			return component.getProperty(this.property).contains(this.value);
		}

		@Override
		public JsonElement toJson(JsonSerializationContext context)
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("property", this.property.name.toString());
			obj.addProperty("operator", "contains");
			obj.add("value", context.serialize(this.value, this.property.contentType));
			return obj;
		}
		
		public static Contains<?> fromJson(JsonObject json, JsonDeserializationContext context, StyleRule.Serializer sr)
		{
			ListPropertyIdentifier<?> property = (ListPropertyIdentifier<?>)sr.getComponentProperty(json.get("property").getAsString());
			return new Contains<>(property, context.deserialize(json.get("value"), property.contentType));
		}
	}
}

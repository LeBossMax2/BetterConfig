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
	String typeName();

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
		public String typeName()
		{
			return "equals";
		}

		@Override
		public String toString()
		{
			return this.property + " equals " + this.value;
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
		public String typeName()
		{
			return "contains";
		}

		@Override
		public String toString()
		{
			return this.property + " contains " + this.value;
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
			IComponent subComponent = component.getProperty(this.property);
			if  (subComponent == null)
				return false;
			
			return this.subSelector.test(subComponent);
		}

		@Override
		public String typeName()
		{
			return "combinator";
		}

		@Override
		public String toString()
		{
			return this.property + " -> " + this.subSelector;
		}
	}

	public static class Serializer implements JsonSerializer<IComponentSelector>, JsonDeserializer<IComponentSelector>
	{
		@Override
		public JsonElement serialize(IComponentSelector src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject json = context.serialize(src, src.getClass()).getAsJsonObject();
			json.addProperty("operator", src.typeName());
			return json;
		}

		@Override
		public IComponentSelector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		        throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();

			switch (obj.get("operator").getAsString())
			{
			case "equals":
				return context.deserialize(obj, Equals.class);
			case "contains":
				return context.deserialize(obj, Contains.class);
			case "combinator":
				return context.deserialize(obj, Combinator.class);
			default:
                throw new JsonParseException("Don't know how to turn " + json + " into a IComponentSelector");
			}
		}
	}
}

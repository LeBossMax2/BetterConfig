package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface ISelector extends Predicate<IPropertySource>
{
	String typeName();

	public static class Not implements ISelector
	{
		private final ISelector subSelector;

		public Not(ISelector subSelector)
		{
			this.subSelector = subSelector;
		}

		@Override
		public boolean test(IPropertySource component)
		{
			return !this.subSelector.test(component);
		}

		@Override
		public String typeName()
		{
			return "not";
		}

		@Override
		public String toString()
		{
			return "not " + this.subSelector;
		}
	}

	public static class And implements ISelector
	{
		private final List<ISelector> subSelectors;

		public And(List<ISelector> subSelectors)
		{
			this.subSelectors = subSelectors;
		}

		@Override
		public boolean test(IPropertySource component)
		{
			return this.subSelectors.stream().allMatch(sel -> sel.test(component));
		}

		@Override
		public String typeName()
		{
			return "and";
		}

		@Override
		public String toString()
		{
			return "and " + this.subSelectors;
		}
	}

	public static class Or implements ISelector
	{
		private final List<ISelector> subSelectors;

		public Or(List<ISelector> subSelectors)
		{
			this.subSelectors = subSelectors;
		}

		@Override
		public boolean test(IPropertySource component)
		{
			return this.subSelectors.stream().anyMatch(sel -> sel.test(component));
		}

		@Override
		public String typeName()
		{
			return "or";
		}

		@Override
		public String toString()
		{
			return "or " + this.subSelectors;
		}
	}

	public static class Equals<T> implements ISelector
	{
		private final PropertyIdentifier<T> property;
		private final T value;

		public Equals(PropertyIdentifier<T> property, T value)
		{
			this.property = property;
			this.value = value;
		}

		@Override
		public boolean test(IPropertySource component)
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

	public static class Contains<T> implements ISelector
	{
		private final ListPropertyIdentifier<T> property;
		private final T value;

		public Contains(ListPropertyIdentifier<T> property, T value)
		{
			this.property = property;
			this.value = value;
		}

		@Override
		public boolean test(IPropertySource component)
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

	public static class Combinator implements ISelector
	{
		private final PropertyIdentifier<? extends IPropertySource> property;
		private final ISelector subSelector;

		public Combinator(PropertyIdentifier<? extends IPropertySource> property, ISelector subSelector)
		{
			this.property = property;
			this.subSelector = subSelector;
		}

		@Override
		public boolean test(IPropertySource component)
		{
			IPropertySource subComponent = component.getProperty(this.property);
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

	public static class Serializer implements JsonSerializer<ISelector>, JsonDeserializer<ISelector>
	{
		@Override
		public JsonElement serialize(ISelector src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject json = context.serialize(src, src.getClass()).getAsJsonObject();
			json.addProperty("operator", src.typeName());
			return json;
		}

		@Override
		public ISelector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		        throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();

			Class<?> operatorClass = getOperatorClass(obj.get("operator").getAsString());

			if (operatorClass == null)
                throw new JsonParseException("Don't know how to turn " + json + " into a IComponentSelector");

			return context.deserialize(obj, operatorClass);
		}

		private static Class<?> getOperatorClass(String operator)
		{
			switch (operator)
			{
			case "not": return Not.class;
			case "and": return And.class;
			case "or": return Or.class;
			case "equals": return Equals.class;
			case "contains": return Contains.class;
			case "combinator": return Combinator.class;
			default: return null;
			}
		}
	}
}

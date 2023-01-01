package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;

public record StyleProperty<T>
(
	ResourceLocation name,
	Type type,
	T defaultValue
)
{
	public StyleProperty
	{ }

	public StyleProperty(ResourceLocation name, T defaultValue)
	{
		this(name, defaultValue.getClass(), defaultValue);
	}

	@Override
	public String toString()
	{
		return this.name.toString() + " (" + this.type + ")";
	}
	
	public static class Serializer implements JsonSerializer<StyleProperty<?>>, JsonDeserializer<StyleProperty<?>>
	{
		private final StyleSerializer parent;

		public Serializer(StyleSerializer parent)
		{
			this.parent = parent;
		}

		@Override
		public JsonElement serialize(StyleProperty<?> src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.name.toString());
		}

		@Override
		public StyleProperty<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return this.parent.getStyleProperty(json.getAsString());
		}
	}
}

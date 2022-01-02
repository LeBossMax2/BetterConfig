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

public class PropertyIdentifier<T>
{
	public final ResourceLocation name;
	public final Class<?> type;

	public PropertyIdentifier(ResourceLocation name, Class<? super T> type)
	{
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString()
	{
		return this.name + " (" + this.type.getName() + ")";
	}
	
	public static class Serializer implements JsonSerializer<PropertyIdentifier<?>>, JsonDeserializer<PropertyIdentifier<?>>
	{
		private final StyleSerializer parent;

		public Serializer(StyleSerializer parent)
		{
			this.parent = parent;
		}

		@Override
		public JsonElement serialize(PropertyIdentifier<?> src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.name.toString());
		}

		@Override
		public PropertyIdentifier<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return this.parent.getComponentProperty(json.getAsString());
		}
	}
}

package fr.max2.betterconfig.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface ISerializableInterface
{
	String typeName();
	
	public static abstract class Serializer implements JsonSerializer<ISerializableInterface>, JsonDeserializer<ISerializableInterface>
	{
		@Override
		public JsonElement serialize(ISerializableInterface src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject json = context.serialize(src, src.getClass()).getAsJsonObject();
			json.addProperty("type", src.typeName());
			return json;
		}

		@Override
		public ISerializableInterface deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		        throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();

			Type operatorType = getConcreteType(obj.get("type").getAsString(), typeOfT);

			if (operatorType == null)
                throw new JsonParseException("Don't know how to turn " + json + " into " + typeOfT);

			return context.deserialize(obj, operatorType);
		}
		
		protected Type getGenericType(Type type, int index)
		{
			if (type instanceof ParameterizedType pt)
				return pt.getActualTypeArguments()[index];
			return null;
		}

		protected abstract Type getConcreteType(String operator, Type interfaceType);
	}
}

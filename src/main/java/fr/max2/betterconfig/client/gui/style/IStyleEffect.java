package fr.max2.betterconfig.client.gui.style;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface IStyleEffect<T>
{
	String typeName();
	
	T updateValue(@Nullable T prevValue, @Nullable T defaultValue);

	public static enum Serializer implements JsonSerializer<IStyleEffect<?>>, JsonDeserializer<IStyleEffect<?>>
	{
		INSTANCE;
		
		@Override
		public JsonElement serialize(IStyleEffect<?> src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject json = context.serialize(src, src.getClass()).getAsJsonObject();
			json.addProperty("operator", src.typeName());
			return json;
		}

		@Override
		public IStyleEffect<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		        throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();

			Type operatorType = getOperatorClass(obj.get("operator").getAsString(), getValueType(typeOfT));

			if (operatorType == null)
                throw new JsonParseException("Don't know how to turn " + json + " into a IStyleEffect");

			return context.deserialize(obj, operatorType);
		}
		
		private static Type getValueType(Type effectType)
		{
			if (effectType instanceof ParameterizedType pt)
				return pt.getActualTypeArguments()[0];
			return null;
		}

		private static Type getOperatorClass(String operator, Type valueType)
		{
			switch (operator)
			{
			case "set": return TypeUtils.parameterize(StyleSetEffect.class, valueType);
			case "item":
				if (((ParameterizedType)valueType).getRawType() != List.class)
					throw new JsonParseException("Type " + valueType + " cannot be indexed");
				return TypeUtils.parameterize(ListIndexEffect.class, getValueType(valueType));
			default: return null;
			}
		}
	}
}

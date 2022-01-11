package fr.max2.betterconfig.client.gui.style.operator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.google.gson.JsonParseException;

import fr.max2.betterconfig.util.ISerializableInterface;

public interface IStyleOperation<T> extends ISerializableInterface
{
	T updateValue(@Nullable T prevValue, @Nullable T defaultValue);

	public static class Serializer extends ISerializableInterface.Serializer
	{
		@Override
		protected Type getConcreteType(String operator, Type interfaceType)
		{
			Type valueType = getGenericType(interfaceType, 0);
			switch (operator)
			{
			case "set": return TypeUtils.parameterize(AssignmentOperation.class, valueType);
			case "item":
				if (((ParameterizedType)valueType).getRawType() != List.class)
					throw new JsonParseException("Type " + valueType + " cannot be indexed");
				return TypeUtils.parameterize(ListIndexingOperation.class, getGenericType(valueType, 0));
			default: return null;
			}
		}
	}
}

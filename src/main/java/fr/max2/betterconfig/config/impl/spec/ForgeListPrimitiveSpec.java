package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.client.util.NumberTypes;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpecVisitor;

public class ForgeListPrimitiveSpec<T> implements IConfigPrimitiveSpec<T>
{
	private final Class<? super T> valueClass;

	public ForgeListPrimitiveSpec(Class<? super T> valueClass)
	{
		this.valueClass = valueClass;
	}

	@Override
	public Class<? super T> getValueClass()
	{
		return this.valueClass;
	}

	@Override
	public boolean isAllowed(T value)
	{
		return true;
	}

	@Override
	public T correct(T value)
	{
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getDefaultValue()
	{
		return (T)this.getType().exploreSpec(DefaultValueVisitor.INSTANCE, this, this.getValueClass());
	}
	
	private static enum DefaultValueVisitor implements IConfigPrimitiveSpecVisitor<Class<?>, Object>
	{
		INSTANCE;
		
		@Override
		public Object visitBoolean(IConfigPrimitiveSpec<Boolean> property, Class<?> param)
		{
			return false;
		}
		
		@Override
		public Object visitNumber(IConfigPrimitiveSpec<? extends Number> property, Class<?> valueClass)
		{
			return NumberTypes.getType(valueClass).parse("0");
		}
		
		@Override
		public Object visitString(IConfigPrimitiveSpec<String> property, Class<?> param)
		{
			return "";
		}
		
		@Override
		public <E extends Enum<E>> Object visitEnum(IConfigPrimitiveSpec<E> property, Class<?> valueClass)
		{
			return valueClass.getEnumConstants()[0];
		}

		@Override
		public Object visitUnknown(IConfigPrimitiveSpec<?> property, Class<?> param)
		{
			return null;
		}
		
	}
}

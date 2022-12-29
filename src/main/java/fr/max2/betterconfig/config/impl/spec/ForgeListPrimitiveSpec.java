package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;

public class ForgeListPrimitiveSpec<T> implements IConfigPrimitiveSpec<T>
{
	private final Class<T> valueClass;

	public ForgeListPrimitiveSpec(Class<T> valueClass)
	{
		this.valueClass = valueClass;
	}

	@Override
	public Class<T> getValueClass()
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

	@Override
	public T getDefaultValue()
	{
		return this.valueClass.cast(this.getType().getDefaultValue(this.valueClass));
	}
}

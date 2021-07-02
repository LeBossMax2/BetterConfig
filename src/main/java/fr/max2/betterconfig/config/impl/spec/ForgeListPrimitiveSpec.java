package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;

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

	@Override
	public T getDefaultValue()
	{
		return null;
	}
}

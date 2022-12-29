package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.PrimitiveType;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;

public class ForgeListPrimitiveSpec<T> implements ConfigPrimitiveSpec.SpecData<T>
{
	private final Class<T> valueClass;

	public ForgeListPrimitiveSpec(Class<T> valueClass)
	{
		this.valueClass = valueClass;
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
		return PrimitiveType.getType(this.valueClass).getDefaultValue();
	}
}

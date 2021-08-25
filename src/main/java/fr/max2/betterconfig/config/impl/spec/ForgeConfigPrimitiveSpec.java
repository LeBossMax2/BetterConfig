package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeConfigPrimitiveSpec<T> implements IConfigPrimitiveSpec<T>
{
	/** The specification */
	private final ValueSpec spec;
	private final Class<? super T> valueClass;

	public ForgeConfigPrimitiveSpec(ValueSpec spec, Class<T> valueClass)
	{
		this.spec = spec;
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
		return this.spec.test(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T correct(T value)
	{
		return (T)this.spec.correct(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getDefaultValue()
	{
		return (T)this.spec.getDefault();
	}
}

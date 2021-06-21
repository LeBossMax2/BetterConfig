package fr.max2.betterconfig.config.impl;

import fr.max2.betterconfig.config.spec.ConfigValueSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeConfigPropertySpec<T> extends ConfigValueSpec<T>
{
	/** The specification */
	private final ValueSpec spec;

	public ForgeConfigPropertySpec(ValueSpec spec)
	{
		super(valueClass(spec));
		this.spec = spec;
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
	
	private static Class<?> valueClass(ValueSpec spec)
	{
		Class<?> specClass = spec.getClazz();
		if (specClass != Object.class)
			return specClass;
		
		Object value = spec.getDefault();
		if (value != null)
			return value.getClass();
		
		return Object.class;
	}
}

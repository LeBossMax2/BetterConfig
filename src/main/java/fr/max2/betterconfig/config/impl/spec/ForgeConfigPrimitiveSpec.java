package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeConfigPrimitiveSpec<T> implements ConfigPrimitiveSpec.SpecData<T>
{
	/** The specification */
	private final ValueSpec spec;
	private final Class<T> valueClass;

	public ForgeConfigPrimitiveSpec(ValueSpec spec, Class<T> valueClass)
	{
		this.spec = spec;
		this.valueClass = valueClass;
	}

	@Override
	public boolean isAllowed(T value)
	{
		return this.spec.test(value);
	}

	@Override
	public T correct(T value)
	{
		return this.valueClass.cast(this.spec.correct(value));
	}

	@Override
	public T getDefaultValue()
	{
		return this.valueClass.cast(this.spec.getDefault());
	}
}

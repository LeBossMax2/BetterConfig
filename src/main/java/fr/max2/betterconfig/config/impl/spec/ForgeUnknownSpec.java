package fr.max2.betterconfig.config.impl.spec;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeUnknownSpec implements IConfigSpecNode
{
	/** The specification */
	private final ValueSpec spec;
	private final Class<?> valueClass;

	public ForgeUnknownSpec(ValueSpec spec, Class<?> valueClass)
	{
		this.spec = spec;
		this.valueClass = valueClass;
	}

	@Override
	public Class<?> getValueClass()
	{
		return this.valueClass;
	}

	@Override
	public Object getDefaultValue()
	{
		return this.spec.getDefault();
	}
}

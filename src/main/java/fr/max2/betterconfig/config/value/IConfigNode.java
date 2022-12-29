package fr.max2.betterconfig.config.value;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.ConfigSpec;

public sealed interface IConfigNode permits ConfigTable, ConfigList, ConfigPrimitive, ConfigUnknown
{
	ConfigSpec getSpec();

	Object getValue();

	void setAsInitialValue();

	void undoChanges();

	public static IConfigNode make(IConfigName identifier, ConfigSpec spec)
	{
		Preconditions.checkNotNull(identifier);
		Preconditions.checkNotNull(spec);

		if (spec instanceof ConfigSpec.Table tableSpec)
		{
			return ConfigTable.make(identifier, tableSpec);
		}
		else if (spec instanceof ConfigSpec.List listSpec)
		{
			return ConfigList.make(identifier, listSpec);
		}
		else if (spec instanceof ConfigSpec.Primitive<?> primitiveSpec)
		{
			return ConfigPrimitive.make(identifier, primitiveSpec);
		}
		else if (spec instanceof ConfigSpec.Unknown unknownSpec)
		{
			return ConfigUnknown.make(identifier, unknownSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
}

package fr.max2.betterconfig.config.value;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.ConfigName;
import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;

public sealed interface ConfigNode permits ConfigTable, ConfigList, ConfigPrimitive, ConfigUnknown
{
	ConfigSpec getSpec();

	Object getValue();

	void setAsInitialValue();

	void undoChanges();

	public static ConfigNode make(ConfigName identifier, ConfigSpec spec)
	{
		Preconditions.checkNotNull(identifier);
		Preconditions.checkNotNull(spec);

		if (spec instanceof ConfigTableSpec tableSpec)
		{
			return ConfigTable.make(identifier, tableSpec);
		}
		else if (spec instanceof ConfigListSpec listSpec)
		{
			return ConfigList.make(identifier, listSpec);
		}
		else if (spec instanceof ConfigPrimitiveSpec<?> primitiveSpec)
		{
			return ConfigPrimitive.make(identifier, primitiveSpec);
		}
		else if (spec instanceof ConfigUnknownSpec unknownSpec)
		{
			return ConfigUnknown.make(identifier, unknownSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
}

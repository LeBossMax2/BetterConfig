package fr.max2.betterconfig.config.value;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;

/**
 * Represents a node holding a value in a configuration tree
 */
public sealed interface ConfigNode permits ConfigTable, ConfigList, ConfigPrimitive, ConfigUnknown
{
	/**
	 * Returns the specification corresponding to this value
	 */
	ConfigSpec getSpec();

	/**
	 * Returns the current value of this node
	 */
	Object getValue();

	/**
	 * Marks the current value of this node as the initial value
	 */
	void setAsInitialValue();

	/**
	 * Resets the value of this node to its initial value
	 */
	void undoChanges();

	/**
	 * Builds a {@code ConfigNode} for the given specification
	 * @param spec the specification of the node to create
	 * @return the newly created node
	 */
	public static ConfigNode make(ConfigSpec spec)
	{
		Preconditions.checkNotNull(spec);

		if (spec instanceof ConfigTableSpec tableSpec)
		{
			return ConfigTable.make(tableSpec);
		}
		else if (spec instanceof ConfigListSpec listSpec)
		{
			return ConfigList.make(listSpec);
		}
		else if (spec instanceof ConfigPrimitiveSpec<?> primitiveSpec)
		{
			return ConfigPrimitive.make(primitiveSpec);
		}
		else if (spec instanceof ConfigUnknownSpec unknownSpec)
		{
			return ConfigUnknown.make(unknownSpec);
		}
		else
		{
			// This should never happen since ConfigSpec is sealed
			throw new UnsupportedOperationException();
		}
	}
}

package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.ConfigSpecNode;

public sealed interface IConfigNode permits ConfigTable, ConfigList, ConfigPrimitive, ConfigUnknown
{
	ConfigSpecNode getSpec();

	Object getValue();

	void setAsInitialValue();

	void undoChanges();
}

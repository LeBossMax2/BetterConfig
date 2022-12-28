package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;

public sealed interface IConfigNode permits ConfigTable, ConfigList, ConfigPrimitive
{
	IConfigSpecNode getSpec();
	
	Object getValue();
	
	void setAsInitialValue();
	
	void undoChanges();
}

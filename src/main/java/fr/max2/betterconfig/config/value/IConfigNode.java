package fr.max2.betterconfig.config.value;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;

public interface IConfigNode
{
	IConfigSpecNode getSpec();
	
	void undoChanges();
}

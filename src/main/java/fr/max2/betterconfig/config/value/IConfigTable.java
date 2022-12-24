package fr.max2.betterconfig.config.value;

import java.util.List;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;

public interface IConfigTable extends IConfigNode
{
	List<Entry> getEntryValues();
	
	@Override
	IConfigTableSpec getSpec();
	
	public static record Entry
	(
		IConfigName key,
		IConfigNode node
	)
	{ }
}

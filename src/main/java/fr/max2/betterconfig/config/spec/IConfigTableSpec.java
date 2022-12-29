package fr.max2.betterconfig.config.spec;

import java.util.List;

import fr.max2.betterconfig.config.ConfigIdentifier;

public interface IConfigTableSpec extends IConfigSpecNode
{
	List<Entry> getEntrySpecs();

	public static record Entry
	(
		ConfigIdentifier key,
		ConfigSpec node
	)
	{ }
}

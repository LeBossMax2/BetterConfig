package fr.max2.betterconfig.config.spec;


/**
 * Represents the specification for a list in a configuration
 */
public final record ConfigListSpec
(
	/** The specification of the possible elements in the list */
	ConfigSpec elementSpec
)
implements ConfigSpec
{ }

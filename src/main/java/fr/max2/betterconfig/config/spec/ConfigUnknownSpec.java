package fr.max2.betterconfig.config.spec;


/**
 * Represents the specification for value with a type not handled by this API in a configuration
 */
public final record ConfigUnknownSpec
(
	/** The object holding implementation-specific additional information about the possible valid configuration values */
	Object specData
)
implements ConfigSpec
{ }

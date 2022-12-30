package fr.max2.betterconfig.config.spec;

/**
 * Represents the specification for a configuration
 */
public sealed interface ConfigSpec permits ConfigUnknownSpec, ConfigTableSpec, ConfigListSpec, ConfigPrimitiveSpec
{
}

package fr.max2.betterconfig.config.spec;

public sealed interface ConfigSpec permits ConfigUnknownSpec, ConfigTableSpec, ConfigListSpec, ConfigPrimitiveSpec
{
}

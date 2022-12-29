package fr.max2.betterconfig.config.spec;

public sealed interface ConfigPrimitiveSpec<T> extends ConfigSpec permits ConfigPrimitiveSpec.Boolean, ConfigPrimitiveSpec.String, ConfigPrimitiveSpec.Enum, ConfigPrimitiveSpec.Number
{
	IConfigPrimitiveSpec<T> node();

	public static <T> ConfigPrimitiveSpec<T> make(IConfigPrimitiveSpec<T> node)
	{
		return node.getType().makeSpec(node);
	}

	public static final record Boolean(IConfigPrimitiveSpec<java.lang.Boolean> node) implements ConfigPrimitiveSpec<java.lang.Boolean>
	{ }

	public static final record String(IConfigPrimitiveSpec<java.lang.String> node) implements ConfigPrimitiveSpec<java.lang.String>
	{ }

	public static final record Enum<E extends java.lang.Enum<E>>(IConfigPrimitiveSpec<E> node) implements ConfigPrimitiveSpec<E>
	{ }

	public static final record Number<N extends java.lang.Number>(IConfigPrimitiveSpec<N> node) implements ConfigPrimitiveSpec<N>
	{ }
}

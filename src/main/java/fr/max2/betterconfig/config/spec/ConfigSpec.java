package fr.max2.betterconfig.config.spec;

public sealed interface ConfigSpec permits ConfigSpec.Unknown, ConfigSpec.Table, ConfigSpec.List, ConfigSpec.Primitive
{
	IConfigSpecNode node();

	public static final record Unknown(IConfigSpecNode node) implements ConfigSpec
	{ }

	public static final record Table(IConfigTableSpec node) implements ConfigSpec
	{ }

	public static final record List(IConfigListSpec node) implements ConfigSpec
	{ }

	public static sealed interface Primitive<T> extends ConfigSpec permits Boolean, String, Enum, Number
	{
		@Override
		IConfigPrimitiveSpec<T> node();

		public static <T> Primitive<T> make(IConfigPrimitiveSpec<T> node)
		{
			return node.getType().makeSpec(node);
		}
	}

	public static final record Boolean(IConfigPrimitiveSpec<java.lang.Boolean> node) implements Primitive<java.lang.Boolean>
	{ }

	public static final record String(IConfigPrimitiveSpec<java.lang.String> node) implements Primitive<java.lang.String>
	{ }

	public static final record Enum<E extends java.lang.Enum<E>>(IConfigPrimitiveSpec<E> node) implements Primitive<E>
	{ }

	public static final record Number<N extends java.lang.Number>(IConfigPrimitiveSpec<N> node) implements Primitive<N>
	{ }
}

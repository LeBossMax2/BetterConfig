package fr.max2.betterconfig.config.spec;

import fr.max2.betterconfig.config.ValueType;

public sealed interface ConfigSpecNode permits ConfigSpecNode.Unknown, ConfigSpecNode.Table, ConfigSpecNode.List, ConfigSpecNode.Primitive
{
	IConfigSpecNode node();

	public static final record Unknown(IConfigSpecNode node) implements ConfigSpecNode
	{ }

	public static final record Table(IConfigTableSpec node) implements ConfigSpecNode
	{ }

	public static final record List(IConfigListSpec node) implements ConfigSpecNode
	{ }

	public static sealed interface Primitive<T> extends ConfigSpecNode permits Boolean, String, Enum, Number
	{
		@Override
		IConfigPrimitiveSpec<T> node();

		public static <T> Primitive<T> make(IConfigPrimitiveSpec<T> node)
		{
			return ValueType.getType(node.getValueClass()).makeSpec(node);
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

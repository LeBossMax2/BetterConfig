package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ValueType<T>
{
	public static final ValueType<?>[] VALUE_TYPES =
	{
		new ValueType<>(Boolean.class, ValueUIBuilder::buildBoolean),
		new ValueType<>(Number.class, ValueUIBuilder::buildNumber),
		new ValueType<>(String.class, ValueUIBuilder::buildString),
		new ValueType<>(Enum.class, ValueUIBuilder::buildEnum),
		new ValueType<>(List.class, ValueUIBuilder::buildList)
	};
	
	private final Class<T> superClass;
	private final BuilderFunction<T> builderFunc;
	
	protected ValueType(Class<T> clazz, BuilderFunction<T> builderFunc)
	{
		this.superClass = clazz;
		this.builderFunc = builderFunc;
	}

	public boolean matches(Class<?> valueClass)
	{
		return this.superClass.isAssignableFrom(valueClass);
	}
	
	public <P> P callBuilder(ValueUIBuilder<P> builder, ValueSpec spec, Object value)
	{
		return this.builderFunc.build(builder, spec, this.superClass.cast(value));
	}

	protected static interface BuilderFunction<T>
	{
		<P> P build(ValueUIBuilder<P> builder, ValueSpec spec, T value);
	}
}
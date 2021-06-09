package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

import fr.max2.betterconfig.ConfigProperty;

public enum ValueType
{
	BOOLEAN(Boolean.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property)
		{
			return builder.buildBoolean((ConfigProperty<Boolean>)property);
		}
	},
	NUMBER(Number.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property)
		{
			return builder.buildNumber((ConfigProperty<? extends Number>)property);
		}
	},
	STRING(String.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property)
		{
			return builder.buildString((ConfigProperty<String>)property);
		}
	},
	ENUM(Enum.class)
	{		
		@SuppressWarnings("unchecked")
		private <E extends Enum<E>, P> P callEnumBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property)
		{
			return builder.buildEnum((ConfigProperty<E>) property);
		}
		
		@Override
		public <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property)
		{
			return callEnumBuilder(builder, property);
		}
	},
	LIST(List.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property)
		{
			return builder.buildList((ConfigProperty<? extends List<?>>)property);
		}
	};
	
	private final Class<?> superClass;
	
	private ValueType(Class<?> clazz)
	{
		this.superClass = clazz;
	}

	public boolean matches(Class<?> valueClass)
	{
		return this.superClass.isAssignableFrom(valueClass);
	}
	
	public abstract <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property);
	
	public static ValueType getType(Class<?> valueClass)
	{
		for (ValueType type : ValueType.values())
		{
			if (type.matches(valueClass))
			{
				return type;
			}
		}
		return null;
	}
}
package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

import fr.max2.betterconfig.ConfigProperty;

/**
 * Represents the type of values inside a config
 */
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
	
	/** The super class corresponding to the type */
	private final Class<?> superClass;
	
	private ValueType(Class<?> clazz)
	{
		this.superClass = clazz;
	}
	
	/**
	 * Checks if the given class is of this type
	 * @param valueClass the class to check
	 * @return true if the class is of this type, false otherwise
	 */
	public boolean matches(Class<?> valueClass)
	{
		return this.superClass.isAssignableFrom(valueClass);
	}
	
	/**
	 * Calls the builder function to build a config property user interface
	 * @param <P> the type of user interface primitives
	 * @param builder
	 * @param property
	 * @return the primitive corresponding to the value user interface
	 */
	public abstract <P> P callBuilder(IValueUIBuilder<P> builder, ConfigProperty<?> property);
	
	/**
	 * Gets the {@code ValueType} corresponding to the given class
	 * @param valueClass
	 * @return the first type that matches the given class
	 */
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
package fr.max2.betterconfig.config;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.config.value.ConfigProperty;
import fr.max2.betterconfig.config.value.IConfigPropertyVisitor;

/**
 * Represents the type of values inside a config
 */
public enum ValueType
{
	BOOLEAN(Boolean.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			return visitor.visitBoolean((ConfigProperty<Boolean>)property, param);
		}
	},
	NUMBER(Number.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			return visitor.visitNumber((ConfigProperty<? extends Number>)property, param);
		}
	},
	STRING(String.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			return visitor.visitString((ConfigProperty<String>)property, param);
		}
	},
	ENUM(Enum.class)
	{		
		@SuppressWarnings("unchecked")
		private <E extends Enum<E>, P, R> R exploreEnum(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			return visitor.visitEnum((ConfigProperty<E>)property, param);
		}
		
		@Override
		public <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			return exploreEnum(visitor, property, param);
		}
	},
	LIST(List.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			return visitor.visitList((ConfigProperty<? extends List<?>>)property, param);
		}
	},
	UNKNOWN(Object.class)
	{
		@Override
		public <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param)
		{
			LOGGER.info("Configuration value of unknown type: " + property.getSpec().getValueClass());
			return visitor.visitUnknown(property, param);
		}
	};
	
	private static final Logger LOGGER = LogManager.getLogger(BetterConfig.MODID);
	
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
	public abstract <P, R> R exploreProperty(IConfigPropertyVisitor<P, R> visitor, ConfigProperty<?> property, P param);
	
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
		
		return UNKNOWN;
	}
}
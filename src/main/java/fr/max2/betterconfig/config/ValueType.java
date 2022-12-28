package fr.max2.betterconfig.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpecVisitor;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;

/**
 * Represents the type of values inside a config
 */
public enum ValueType
{
	BOOLEAN(Boolean.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param)
		{
			return visitor.visitBoolean((IConfigPrimitiveSpec<Boolean>)property, param);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param)
		{
			return visitor.visitBoolean((ConfigPrimitive<Boolean>)property, param);
		}
	},
	NUMBER(Number.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param)
		{
			return visitor.visitNumber((IConfigPrimitiveSpec<? extends Number>)property, param);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param)
		{
			return visitor.visitNumber((ConfigPrimitive<? extends Number>)property, param);
		}
	},
	STRING(String.class)
	{
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param)
		{
			return visitor.visitString((IConfigPrimitiveSpec<String>)property, param);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <P, R> R exploreProperty(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param)
		{
			return visitor.visitString((ConfigPrimitive<String>)property, param);
		}
	},
	ENUM(Enum.class)
	{
		@SuppressWarnings("unchecked")
		private <E extends Enum<E>, P, R> R exploreEnumSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param)
		{
			return visitor.visitEnum((IConfigPrimitiveSpec<E>)property, param);
		}
		
		@Override
		public <P, R> R exploreSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param)
		{
			return exploreEnumSpec(visitor, property, param);
		}
		
		@SuppressWarnings("unchecked")
		private <E extends Enum<E>, P, R> R exploreEnumProp(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param)
		{
			return visitor.visitEnum((ConfigPrimitive<E>)property, param);
		}
		
		@Override
		public <P, R> R exploreProperty(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param)
		{
			return exploreEnumProp(visitor, property, param);
		}
	},
	UNKNOWN(Object.class)
	{
		@Override
		public <P, R> R exploreSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param)
		{
			LOGGER.info("Configuration value of unknown type: " + property.getValueClass());
			return visitor.visitUnknown(property, param);
		}
		
		@Override
		public <P, R> R exploreProperty(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param)
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
	public <R> R exploreProperty(IConfigPrimitiveVisitor<Void, R> visitor, ConfigPrimitive<?> property)
	{
		return this.exploreProperty(visitor, property, null);
	}
	
	/**
	 * Calls the builder function to build a config property user interface
	 * @param <P> the type of user interface primitives
	 * @param builder
	 * @param property
	 * @return the primitive corresponding to the value user interface
	 */
	public abstract <P, R> R exploreProperty(IConfigPrimitiveVisitor<P, R> visitor, ConfigPrimitive<?> property, P param);
	
	/**
	 * Calls the builder function to build a config property user interface
	 * @param <P> the type of user interface primitives
	 * @param builder
	 * @param property
	 * @return the primitive corresponding to the value user interface
	 */
	public <R> R exploreSpec(IConfigPrimitiveSpecVisitor<Void, R> visitor, IConfigPrimitiveSpec<?> property)
	{
		return this.exploreSpec(visitor, property, null);
	}
	
	/**
	 * Calls the builder function to build a config property user interface
	 * @param <P> the type of user interface primitives
	 * @param builder
	 * @param property
	 * @return the primitive corresponding to the value user interface
	 */
	public abstract <P, R> R exploreSpec(IConfigPrimitiveSpecVisitor<P, R> visitor, IConfigPrimitiveSpec<?> property, P param);
	
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
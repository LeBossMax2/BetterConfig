package fr.max2.betterconfig.config;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

/**
 * A property for a configuration value and its specification
 * @param <T> the type of value contained in the property
 */
public class ConfigProperty<T> extends ConfigNode
{
	private static final Logger LOGGER = LogManager.getLogger(BetterConfig.MODID);
	
	/** The specification */
	private final ValueSpec spec;
	/** The configuration value currently saved */
	private final ConfigValue<T> configValue;
	/** The function to call then the value is changed */
	private final Consumer<ConfigProperty<?>> changeListener;
	/** The current temporary value of the property */
	private T currentValue;
	
	public ConfigProperty(ValueSpec spec, ConfigValue<T> configValue, Consumer<ConfigProperty<?>> changeListener)
	{
		super(configValue.getPath());
		this.spec = spec;
		this.configValue = configValue;
		this.changeListener = changeListener;
		this.currentValue = configValue.get();
	}
	
	/**
	 * Gets the display name of the property
	 */
	public ITextComponent getDisplayName()
	{
		// Try getting name from translation key 
		String translationKey = this.spec.getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey))
			return new TranslationTextComponent(translationKey);
		
		// Try getting name from path
		List<String> path = this.getPath();
		if (!path.isEmpty())
			return new StringTextComponent(path.get(path.size() - 1));
		
		// No name found :(
		return StringTextComponent.EMPTY;
	}
	
	/**
	 * Checks if the given value is a valid value
	 * @param value the value to check
	 * @return true if the value matches the spec, false otherwise
	 */
	public boolean isAllowed(T value)
	{
		return this.spec.test(value);
	}
	
	/**
	 * Correct the given value to match the spec
	 * @param value the value to fix
	 * @return a valid value
	 */
	@SuppressWarnings("unchecked")
	public T correct(T value)
	{
		return (T)this.spec.correct(value);
	}
	
	/**
	 * Gets the current configuration value
	 * @return the current temporary value
	 */
	public T getValue()
	{
		return this.currentValue;
	}
	
	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	public void setValue(T value)
	{
		if (!Objects.equals(this.getValue(), value))
		{
			this.currentValue = value;
			this.changeListener.accept(this);
		}
	}
	
	/**
	 * Gets the default configuration value
	 */
	@SuppressWarnings("unchecked")
	public T getDefaultValue()
	{
		return (T)this.spec.getDefault();
	}
	
	/**
	 * Checks if the value changed compared to the saved value
	 * @return true if the value is different, false otherwise
	 */
	public boolean valueChanged()
	{
		return !Objects.equals(this.configValue.get(), this.currentValue);
	}
	
	/**
	 * Saves the changes to the configuration file
	 */
	public void sendChanges()
	{
		if (this.valueChanged())
			this.configValue.set(this.currentValue);
	}
	
	@Override
	String getCommentString()
	{
		return this.spec.getComment();
	}
	
	/**
	 * Gets the class of the configuration value
	 */
	public Class<?> getValueClass()
	{
		Class<?> specClass = spec.getClazz();
		if (specClass != Object.class)
			return specClass;
		
		T value = this.getValue();
		if (value != null)
			return value.getClass();
		
		return Object.class;
	}
	
	/**
	 * Tries to cast the property to the given type
	 * @param <R> the desired type
	 * @param clazz the class of the desired type
	 * @return An empty optional if the conversion cannot be performed, the converted property otherwise
	 */
	@SuppressWarnings("unchecked")
	public <R> Optional<ConfigProperty<? extends R>> tryCast(Class<R> clazz)
	{
		if (clazz.isAssignableFrom(this.getValueClass()))
		{
			return Optional.of((ConfigProperty<? extends R>)this);
		}
		return Optional.empty();
	}
	
	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <R> R explore(IConfigPropertyVisitor<Void, R> visitor)
	{
		return this.explore(visitor, null);
	}

	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <P, R> R explore(IConfigPropertyVisitor<P, R> visitor, P param)
	{
		Class<?> specClass = this.getValueClass();
		ValueType type = ValueType.getType(specClass);
		
		if (type == null)
		{
			LOGGER.info("Configuration value of unknown type: " + specClass);
			return visitor.visitUnknown(this, param);
		}

		return type.exploreProperty(visitor, this, param);
	}
	
}

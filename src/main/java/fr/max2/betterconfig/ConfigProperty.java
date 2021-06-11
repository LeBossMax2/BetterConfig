package fr.max2.betterconfig;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

/**
 * A property for a configuration value and its specification
 * @param <T> the type of value contained in the property
 */
public class ConfigProperty<T>
{
	/** The specification */
	private final ValueSpec spec;
	/** The configuration value */
	private final ConfigValue<T> configValue;
	/** The comments describing the property */
	private List<? extends ITextComponent> commentLines = null;
	
	public ConfigProperty(ValueSpec spec, ConfigValue<T> configValue)
	{
		this.spec = spec;
		this.configValue = configValue;
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
		List<String> path = this.configValue.getPath();
		if (!path.isEmpty())
			return new StringTextComponent(path.get(path.size() - 1));
		
		// No name found :(
		return StringTextComponent.EMPTY;
	}
	
	/**
	 * Gets the path of this value in configuration spec
	 * @return
	 */
	public List<String> getPath()
	{
		return this.configValue.getPath();
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
	 */
	public T getValue()
	{
		return this.configValue.get();
	}
	
	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	public void setValue(T value)
	{
		if (!Objects.equals(this.getValue(), value))
			this.configValue.set(value);
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
	 * Gets the comment associated with the property
	 * @return an unmodifiable list containing the comments
	 */
	public List<? extends ITextComponent> getDisplayComment()
	{
		if (this.commentLines == null)
		{
			String comment = this.spec.getComment();
			if (Strings.isNullOrEmpty(comment))
			{
				this.commentLines = Collections.emptyList();
			}
			else
			{
				this.commentLines = Stream.of(comment.split("\n")).map(StringTextComponent::new).collect(Collectors.toList());
			}
		}
		return this.commentLines;
	}
	
	/**
	 * Gets the class of the configuration value
	 */
	public Class<?> getValueClass()
	{
		Class<?> specClass = spec.getClazz();
		if (specClass != Object.class)
			return specClass;
		
		T value = this.configValue.get();
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
	
}

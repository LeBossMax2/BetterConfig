package fr.max2.betterconfig.config.value;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import fr.max2.betterconfig.config.spec.ConfigPropertySpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigProperty<T> extends ConfigNode<ConfigPropertySpec<T>>
{
	/** The configuration value currently saved */
	private final ConfigValue<T> configValue;
	/** The function to call then the value is changed */
	private final Consumer<ConfigProperty<?>> changeListener;
	/** The current temporary value of the property */
	private T currentValue;
	
	public ConfigProperty(ConfigPropertySpec<T> spec, ConfigValue<T> configValue, Consumer<ConfigProperty<?>> changeListener)
	{
		super(spec);
		this.configValue = configValue;
		this.changeListener = changeListener;
		this.currentValue = configValue.get();
	}
	
	/**
	 * Gets the current configuration value
	 * @return the current temporary value
	 */
	public T getValue()
	{
		return deepCopy(this.currentValue);
	}
	
	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	public void setValue(T value)
	{
		if (!Objects.equals(this.getValue(), value))
		{
			this.currentValue = deepCopy(value);
			this.changeListener.accept(this);
		}
	}
	
	/**
	 * Checks if the value changed compared to the saved value
	 * @return true if the value is different, false otherwise
	 */
	public boolean valueChanged()
	{
		return !Objects.equals(this.configValue.get(), this.getValue());
	}
	
	/**
	 * Saves the changes to the configuration file
	 */
	public void sendChanges()
	{
		if (this.valueChanged())
			this.configValue.set(this.getValue());
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
		if (clazz.isAssignableFrom(this.getSpec().getValueClass()))
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
	public <R> R exploreType(IConfigPropertyVisitor<Void, R> visitor)
	{
		return this.exploreType(visitor, null);
	}

	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <P, R> R exploreType(IConfigPropertyVisitor<P, R> visitor, P param)
	{
		return this.getSpec().getType().exploreProperty(visitor, this, param);
	}
	
	@Override
	public <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitProperty(this, param);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T deepCopy(T value)
	{
		if (!(value instanceof List))
			return value;
		
		List<?> list = (List<?>)value;
		return (T)list.stream().map(ConfigProperty::deepCopy).collect(Collectors.toList());
	}
	
}

package fr.max2.betterconfig.config.value;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class ConfigProperty<T>
{
	protected final ConfigValue<T> value;
	/** The function to call then the value is changed */
	protected final Consumer<ConfigProperty<?>> changeListener;
	
	public ConfigProperty(ConfigValue<T> value, Consumer<ConfigProperty<?>> changeListener)
	{
		this.value = value;
		this.changeListener = changeListener;
	}
	
	public void onValueChanged()
	{
		this.changeListener.accept(this);
	}
	/**
	 * Checks if the value changed compared to the saved value
	 * @return true if the value is different, false otherwise
	 */
	public boolean valueChanged()
	{
		return !Objects.equals(this.getSavedValue(), this.value.getValue());
	}
	
	/**
	 * Saves the changes to the configuration file
	 */
	public void sendChanges()
	{
		if (this.valueChanged())
			this.setSavedValue(this.value.getValue());
	}
	
	public ConfigValue<T> getValue()
	{
		return this.value;
	}
	
	protected abstract void setSavedValue(T value);
	protected abstract T getSavedValue();
}

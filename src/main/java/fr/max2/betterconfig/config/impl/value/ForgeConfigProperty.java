package fr.max2.betterconfig.config.impl.value;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigProperty<T>
{
	/** The configuration value currently saved */
	private final ConfigValue<T> configValue;
	private final Supplier<T> valueProvider;
	/** The function to call then the value is changed */
	private final Consumer<ForgeConfigProperty<?>> changeListener;
	
	public ForgeConfigProperty(ConfigValue<T> configValue, Consumer<ForgeConfigProperty<?>> changeListener, Supplier<T> valueProvider)
	{
		this.configValue = configValue;
		this.changeListener = changeListener;
		this.valueProvider = valueProvider;
	}
	
	protected void onValueChanged()
	{
		this.changeListener.accept(this);
	}
	/**
	 * Checks if the value changed compared to the saved value
	 * @return true if the value is different, false otherwise
	 */
	public boolean valueChanged()
	{
		return !Objects.equals(this.getSavedValue(), this.getCurrentValue());
	}
	
	/**
	 * Saves the changes to the configuration file
	 */
	public void sendChanges()
	{
		if (this.valueChanged())
			this.setSavedValue(this.getCurrentValue());
	}
	
	private void setSavedValue(T value)
	{
		this.configValue.set(value);
	}
	
	private T getSavedValue()
	{
		return this.configValue.get();
	}
	
	private T getCurrentValue()
	{
		return this.valueProvider.get();
	}
}

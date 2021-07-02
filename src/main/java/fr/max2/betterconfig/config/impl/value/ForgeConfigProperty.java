package fr.max2.betterconfig.config.impl.value;

import java.util.Objects;
import java.util.function.Consumer;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class ForgeConfigProperty<Spec extends IConfigSpecNode, T> extends ForgeConfigNode<Spec>
{
	/** The configuration value currently saved */
	private final ConfigValue<T> configValue;
	
	public ForgeConfigProperty(Spec spec, Consumer<ForgeConfigProperty<?, ?>> changeListener, ConfigValue<T> configValue)
	{
		super(spec, changeListener);
		this.configValue = configValue;
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
	
	protected abstract T getCurrentValue();
}

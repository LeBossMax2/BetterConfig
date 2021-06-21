package fr.max2.betterconfig.config.impl;

import java.util.function.Consumer;

import fr.max2.betterconfig.config.spec.ConfigValueSpec;
import fr.max2.betterconfig.config.value.ConfigProperty;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigProperty<T> extends ConfigProperty<T>
{
	/** The configuration value currently saved */
	private final ConfigValue<T> configValue;

	public ForgeConfigProperty(ConfigValueSpec<T> spec, ConfigValue<T> configValue, Consumer<ConfigProperty<?>> changeListener)
	{
		super(spec, changeListener, configValue.get());
		this.configValue = configValue;
	}

	@Override
	protected void setSavedValue(T value)
	{
		this.configValue.set(value);
	}

	@Override
	protected T getSavedValue()
	{
		return this.configValue.get();
	}
}

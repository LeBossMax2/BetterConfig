package fr.max2.betterconfig.config.impl.value;

import java.util.Objects;
import java.util.function.Consumer;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigPrimitive<T> extends ForgeConfigProperty<IConfigPrimitiveSpec<T>, T> implements IConfigPrimitive<T>
{
	/** The current temporary value of the property */
	protected T currentValue;

	public ForgeConfigPrimitive(IConfigPrimitiveSpec<T> spec, Consumer<ForgeConfigProperty<?, ?>> changeListener, ConfigValue<T> configValue)
	{
		super(spec, changeListener, configValue);
		this.currentValue = configValue.get();
	}
	
	@Override
	public T getValue()
	{
		return this.currentValue;
	}
	
	public void setValue(T value)
	{
		if (!Objects.equals(this.getValue(), value))
		{
			this.currentValue = value;
			this.onValueChanged();
		}
	}

	@Override
	protected T getCurrentValue()
	{
		return this.getValue();
	}
}

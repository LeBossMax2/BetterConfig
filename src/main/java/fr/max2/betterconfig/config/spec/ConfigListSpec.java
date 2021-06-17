package fr.max2.betterconfig.config.spec;

import java.util.List;

import fr.max2.betterconfig.config.ValueType;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ConfigListSpec<T> extends ConfigPropertySpec<List<T>>
{
	/** The type of the elements of the list */
	private final ValueType elementType;
	private final ConfigPropertySpec<T> elementSpec;
	
	public ConfigListSpec(ConfigLocation loc, ValueSpec spec)
	{
		super(loc, spec);
		this.elementType = ValueType.getType(this.getElementClass());
	}
	
	/**
	 * Gets the class of the configuration value
	 */
	public Class<?> getElementClass()
	{
		List<T> value = this.getDefaultValue();
		if (value != null)
			return value.getClass();
		
		return Object.class;
	}
	
	public ConfigPropertySpec<T> getElementSpec()
	{
		return elementSpec;
	}
}

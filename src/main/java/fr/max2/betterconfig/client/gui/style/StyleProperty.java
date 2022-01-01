package fr.max2.betterconfig.client.gui.style;

import net.minecraft.resources.ResourceLocation;

public class StyleProperty<T>
{
	public final ResourceLocation name;
	public final Class<?> type;
	public final T defaultValue;

	public StyleProperty(ResourceLocation name, Class<?> type, T defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public StyleProperty(ResourceLocation name, T defaultValue)
	{
		this(name, defaultValue.getClass(), defaultValue);
	}
	
	@Override
	public String toString()
	{
		return this.name.toString() + " (" + this.type.getName() + ")";
	}
}

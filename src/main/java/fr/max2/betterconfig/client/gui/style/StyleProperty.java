package fr.max2.betterconfig.client.gui.style;

import net.minecraft.resources.ResourceLocation;

public class StyleProperty<T>
{
	public final ResourceLocation name;
	public final T defaultValue;

	public StyleProperty(ResourceLocation name, T defaultValue)
	{
		this.name = name;
		this.defaultValue = defaultValue;
	}
}

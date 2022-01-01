package fr.max2.betterconfig.client.gui.style;

import net.minecraft.resources.ResourceLocation;

public class PropertyIdentifier<T>
{
	public final ResourceLocation name;
	public final Class<?> type;

	public PropertyIdentifier(ResourceLocation name, Class<? super T> type)
	{
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString()
	{
		return this.name + " (" + this.type.getName() + ")";
	}
}

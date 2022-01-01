package fr.max2.betterconfig.client.gui.style;

import java.util.List;

import net.minecraft.resources.ResourceLocation;

public class ListPropertyIdentifier<T> extends PropertyIdentifier<List<T>>
{
	public final Class<?> contentType;

	public ListPropertyIdentifier(ResourceLocation name, Class<?> contentType)
	{
		super(name, List.class);
		this.contentType = contentType;
	}

	@Override
	public String toString()
	{
		return this.name + " (List<" + this.contentType.getName() + ">)";
	}
}

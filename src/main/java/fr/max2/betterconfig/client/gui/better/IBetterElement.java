package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.resources.ResourceLocation;

/** An interface for ui elements with a simple layout system */
public interface IBetterElement extends IComponent
{
	public static final PropertyIdentifier<Boolean> FILTERED_OUT = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "filtered_out"), Boolean.class);
	
	boolean filterElements(ConfigFilter filter);
}
package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.resources.ResourceLocation;

public class Constants
{
	public static final ResourceLocation BETTER_ICONS = new ResourceLocation(BetterConfig.MODID, "textures/gui/better_icons.png");
	
	/** The default color of the text in a text field */
	public static final int DEFAULT_FIELD_TEXT_COLOR = 0xFF_E0_E0_E0;
	/** The color of the text in a text field when the value is not valid */
	public static final int ERROR_FIELD_TEXT_COLOR   = 0xFF_FF_00_00;
}

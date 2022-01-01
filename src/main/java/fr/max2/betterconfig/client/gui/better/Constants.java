package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.resources.ResourceLocation;

public class Constants
{
	public static final ResourceLocation BETTER_ICONS = new ResourceLocation(BetterConfig.MODID, "textures/gui/better_icons.png");
	
	/** The width of the indentation added for each nested section */
	public static final int SECTION_TAB_SIZE = 22;
	/** The left and right padding around the screen */
	public static final int X_PADDING = 10;
	/** The top and bottom padding around the screen */
	public static final int Y_PADDING = 10;
	/** The right padding around the screen */
	public static final int RIGHT_PADDING = 10;
	/** The height of the value entries */
	public static final int VALUE_CONTAINER_HEIGHT = 24;
	/** The height of the value widget */
	public static final int VALUE_HEIGHT = 20;
	/** The width of the value widget */
	public static final int VALUE_WIDTH = 150;
	
	/** The default color of the text in a text field */
	public static final int DEFAULT_FIELD_TEXT_COLOR = 0xFF_E0_E0_E0;
	/** The color of the text in a text field when the value is not valid */
	public static final int ERROR_FIELD_TEXT_COLOR   = 0xFF_FF_00_00;
}

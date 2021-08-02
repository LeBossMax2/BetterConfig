package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.util.ResourceLocation;

public class Constants
{
	/** The translation key for displaying the default value */
	public static final String DEFAULT_VALUE_KEY = BetterConfig.MODID + ".option.default_value";
	/** The translation key for the text field of the search bar */
	public static final String SEARCH_BAR_KEY = BetterConfig.MODID + ".option.search";
	/** The translation key for the text on the add element button in lists */
	public static final String ADD_ELEMENT_KEY = BetterConfig.MODID + ".list.add";
	/** The translation key for the tooltip on the add element button at the start of lists */
	public static final String ADD_FIRST_TOOLTIP_KEY = BetterConfig.MODID + ".list.add.first.tooltip";
	/** The translation key for the tooltip on the add element button at the end of lists */
	public static final String ADD_LAST_TOOLTIP_KEY = BetterConfig.MODID + ".list.add.last.tooltip";
	/** The translation key for the tooltop of the button to remove elements from the list */
	public static final String REMOVE_TOOLTIP_KEY = BetterConfig.MODID + ".list.remove.tooltip";
	/** The translation key for the tooltop of undo button */
	public static final String UNDO_TOOLTIP_KEY = BetterConfig.MODID + ".undo.tooltip";
	/** The translation key for the tooltop of reset button */
	public static final String RESET_TOOLTIP_KEY = BetterConfig.MODID + ".reset.tooltip";
	
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

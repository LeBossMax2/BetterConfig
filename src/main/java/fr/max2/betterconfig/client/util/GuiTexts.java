package fr.max2.betterconfig.client.util;

import fr.max2.betterconfig.BetterConfig;

public final class GuiTexts
{
	private GuiTexts()
	{ }
	
	public static final String CANCEL_CONFIG_KEY = BetterConfig.MODID + ".config.cancel";
	public static final String SAVE_CONFIG_KEY = "gui.done";
	
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
}

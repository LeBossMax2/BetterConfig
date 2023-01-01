package fr.max2.betterconfig.client.util;

import fr.max2.betterconfig.BetterConfig;

/**
 * This class contains all the translation keys used in the mod
 */
public final class GuiTexts
{
	private GuiTexts()
	{ }

	/** The title of the configuration screen */
	public static final String CONFIG_SCREEN_TITLE_KEY = BetterConfig.MODID + ".screen.title";
	/** The tooltip of the configuration button */
	public static final String CONFIG_TOOLTIP_KEY = BetterConfig.MODID + ".config.tooltip";
	/** The text on the common configuration button */
	public static final String COMMON_CONFIG_KEY = BetterConfig.MODID + ".config.common";
	/** The text on the client configuration button */
	public static final String CLIENT_CONFIG_KEY = BetterConfig.MODID + ".config.client";
	/** The text on the server configuration button */
	public static final String SERVER_CONFIG_KEY = BetterConfig.MODID + ".config.server";

	/** The text on the cancel button */
	public static final String CANCEL_CONFIG_KEY = BetterConfig.MODID + ".config.cancel";
	/** The text on the save button */
	public static final String SAVE_CONFIG_KEY = "gui.done";
	/** The text of the undo button */
	public static final String UNDO_BUTTON_KEY = BetterConfig.MODID + ".undo";
	/** The tooltip of the undo button */
	public static final String UNDO_TOOLTIP_KEY = BetterConfig.MODID + ".undo.tooltip";
	/** The tooltip of the reset button */
	public static final String RESET_TOOLTIP_KEY = BetterConfig.MODID + ".reset.tooltip";

	/** The text to show when no option is selected */
	public static final String NO_OPTION_KEY = BetterConfig.MODID + ".option.no_value";
	/** The text to show when the selected option is true */
	public static final String TRUE_OPTION_KEY = BetterConfig.MODID + ".option.true";
	/** The text to show when the selected option is false */
	public static final String FALSE_OPTION_KEY = BetterConfig.MODID + ".option.false";
	/** Displaying the default value */
	public static final String DEFAULT_VALUE_KEY = BetterConfig.MODID + ".option.default_value";
	/** The text field of the search bar */
	public static final String SEARCH_BAR_KEY = BetterConfig.MODID + ".option.search";

	/** The label of elements of a list */
	public static final String LIST_ELEMENT_LABEL_KEY = BetterConfig.MODID + ".list.child";
	/** The text on the add element button in lists */
	public static final String ADD_ELEMENT_KEY = BetterConfig.MODID + ".list.add";
	/** The tooltip of the add element button at the start of lists */
	public static final String ADD_FIRST_TOOLTIP_KEY = BetterConfig.MODID + ".list.add.first.tooltip";
	/** The tooltip of the add element button at the end of lists */
	public static final String ADD_LAST_TOOLTIP_KEY = BetterConfig.MODID + ".list.add.last.tooltip";
	/** The text on the button to remove elements from the list */
	public static final String REMOVE_ELEMENT_KEY = BetterConfig.MODID + ".list.remove";
	/** The tooltip of the button to remove elements from the list */
	public static final String REMOVE_TOOLTIP_KEY = BetterConfig.MODID + ".list.remove.tooltip";

	// Narration

	public static final String SECTION_TITLE_SHOWN = BetterConfig.MODID + ".narration.section.title.shown";
	public static final String SECTION_TITLE_COLLAPSED = BetterConfig.MODID + ".narration.section.title.collapsed";
	public static final String SECTION_USAGE_FOCUSED = BetterConfig.MODID + ".narration.section.usage.focused";
	public static final String SECTION_USAGE_HOVERED = BetterConfig.MODID + ".narration.section.usage.hovered";
}

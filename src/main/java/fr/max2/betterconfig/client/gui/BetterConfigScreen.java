package fr.max2.betterconfig.client.gui;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.client.gui.component.ComponentScreen;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import fr.max2.betterconfig.client.gui.style.StyleSheetManager;
import fr.max2.betterconfig.config.impl.value.ForgeConfigProperty;
import fr.max2.betterconfig.config.impl.value.ForgeConfig;
import fr.max2.betterconfig.config.value.ConfigTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;

public class BetterConfigScreen extends ComponentScreen
{
	private static final Logger LOGGER = LogManager.getLogger();

	/** The config user interface builder */
	private final IConfigUIBuilder uiBuilder;
	/** The mod this configuration is from */
	private final ModContainer mod;

	/** The list of available configurations in the mod */
	private final List<ModConfig> modConfigs;
	/** The current edited configuration table */
	private final ConfigTable[] currentTables;
	/** The set of properties that changed and need to be saved */
	private final Set<ForgeConfigProperty<?>> modifiedProperties = new HashSet<>();
	/** The index of the current edited configuration in the list */
	private int configIndex;

	/** The screen to open when this screen closes */
	private Screen prevScreen = null;

	private boolean autoSave = true;

	protected BetterConfigScreen(IConfigUIBuilder uiBuilder, StyleSheet styleSheet, ModContainer mod, List<ModConfig> configs, int index)
	{
		// TODO change to translatable
		super(Component.literal(mod.getModId() + " configuration : " + configs.get(index).getFileName()), styleSheet);
		this.uiBuilder = uiBuilder;
		this.mod = mod;
		this.modConfigs = configs;
		this.configIndex = index;
		this.currentTables = new ConfigTable[configs.size()];
	}

	@Override
	protected void init()
	{
		if (this.currentTables[this.configIndex] == null)
			this.currentTables[this.configIndex] = ForgeConfig.make(this.modConfigs.get(this.configIndex).<ForgeConfigSpec>getSpec().self(), this::onPropertyChanged);
		// Builds the user interface
		this.setContent(this.uiBuilder.build(this, this.currentTables[this.configIndex]));
	}

	@Override
	public void onClose()
	{
		// Open back the previous screen
		this.minecraft.setScreen(this.prevScreen);
	}

	@Override
	public void removed()
	{
		super.removed();
		if (this.autoSave)
			this.saveChanges();
	}

	public void saveChanges()
	{
		if (this.configChanged())
		{
			// Save changes to config
			for (ForgeConfigProperty<?> property : this.modifiedProperties)
			{
				property.sendChanges();
			}
			this.modifiedProperties.clear();
			// After a save, ConfigFileTypeHandler automatically sends config update event if you're lucky
			this.getCurrentConfig().save();
		}
	}

	public void cancelChanges()
	{
		if (this.configChanged())
		{
			this.autoSave = false;
			// Reopen gui
			BetterConfigScreen newScreen = new BetterConfigScreen(this.uiBuilder, this.getStyleSheet(), this.mod, this.modConfigs, this.configIndex);
			newScreen.setPrevScreen(this.prevScreen);
			this.minecraft.setScreen(newScreen);
		}
	}

	/**
	 * Gets whether the configuration has been edited
	 * @return true if the configuration changed, false otherwise
	 */
	protected boolean configChanged()
	{
		return !this.modifiedProperties.isEmpty();
	}

	/**
	 * Called when a the value property changes to tracks which property should be saved
	 * @param property the property that changed
	 */
	protected void onPropertyChanged(ForgeConfigProperty<?> property)
	{
		if (property.valueChanged())
		{
			this.modifiedProperties.add(property);
		}
		else
		{
			this.modifiedProperties.remove(property);
		}
	}

	/**
	 * Sets the screen to show when this screen closes
	 * @param prevScreen the screen to show after
	 */
	public void setPrevScreen(Screen prevScreen)
	{
		this.prevScreen = prevScreen;
	}

	/**
	 * Opens the configuration with the given index in the list
	 * @param index the index of the configuration to edit
	 */
	public void openConfig(int index)
	{
		Preconditions.checkElementIndex(index, this.modConfigs.size(), "index must be insize mod config list");
		this.configIndex = index;
		this.init(this.minecraft, this.width, this.height);

	}

	/** Gets the mod this configuration is from */
	public ModContainer getMod()
	{
		return this.mod;
	}

	/** Gets the list of available configurations in the mod */
	public List<ModConfig> getModConfigs()
	{
		return this.modConfigs;
	}

	/** Gets the index of the current edited configuration in the list */
	public int getCurrentConfigIndex()
	{
		return this.configIndex;
	}

	/** Gets the current edited configuration */
	public ModConfig getCurrentConfig()
	{
		return this.modConfigs.get(this.configIndex);
	}

	/** Gets the font renderer of the screen */
	public Font getFont()
	{
		return this.font;
	}

	/**
	 * Gets a factory creating a configuration GUI for the given mod
	 * @param mod
	 * @return a function to build the GUI
	 */
	public static BiFunction<Minecraft, Screen, Screen> factory(ModContainer mod, List<ModConfig> configs)
	{
		return (mc, prevScreen) ->
		{
			BetterConfigScreen screen = buildScreen(mod, configs);
			screen.setPrevScreen(prevScreen);
			return screen;
		};
	}

	private static BetterConfigScreen buildScreen(ModContainer mod, List<ModConfig> configs)
	{
		IModInfo modInfo = mod.getModInfo();

		Object styleSheetLocation = modInfo.getModProperties().get("betterconfig_stylesheet");
		if (styleSheetLocation == null)
			styleSheetLocation = modInfo.getConfig().getConfigElement("betterconfig_stylesheet").orElse(null);

		ResourceLocation styleSheetLoc;
		if (styleSheetLocation instanceof String loc)
		{
			styleSheetLoc = new ResourceLocation(loc);
		}
		else
		{
			if (styleSheetLocation != null)
				LOGGER.warn("Mod parameter 'betterconfig_stylesheet' of wrong type (Expected string) for mod: {}: {}", mod.getModId(), styleSheetLocation.getClass().getTypeName());

			styleSheetLoc = StyleSheet.DEFAULT_STYLESHEET;
		}

		StyleSheet styleSheet;
		try
		{
			styleSheet = StyleSheetManager.INSTANCE.getStyleSheet(styleSheetLoc);
		}
		catch (IOException e)
		{
			LOGGER.error("Exception loading stylesheet: {}: {}", styleSheetLoc, e);
			throw new RuntimeException("Exception loading stylesheet: " + styleSheetLoc, e);
		}

		// TODO [#2] Get ui builder from mod properties
		IConfigUIBuilder uiBuilder = IConfigUIBuilder.DEFAULT;

		return new BetterConfigScreen(uiBuilder, styleSheet, mod, configs, 0);
	}
}

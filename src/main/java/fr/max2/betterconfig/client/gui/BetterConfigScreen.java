package fr.max2.betterconfig.client.gui;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.client.gui.better.BetterConfigBuilder;
import fr.max2.betterconfig.client.gui.component.ComponentScreen;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import fr.max2.betterconfig.config.impl.value.ForgeConfigProperty;
import fr.max2.betterconfig.config.impl.value.ForgeConfigTable;
import fr.max2.betterconfig.config.value.IConfigTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

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
	private final IConfigTable[] currentTables;
	/** The set of properties that changed and need to be saved */
	private final Set<ForgeConfigProperty<?>> modifiedProperties = new HashSet<>();
	/** The index of the current edited configuration in the list */
	private int configIndex;
	
	/** The screen to open when this screen closes */
	private Screen prevScreen = null;
	
	private boolean autoSave = true;
	
	protected BetterConfigScreen(IConfigUIBuilder uiBuilder, StyleSheet styleSheet, ModContainer mod, List<ModConfig> configs, int index)
	{
		super(new TextComponent(mod.getModId() + " configuration : " + configs.get(index).getFileName()), styleSheet);
		this.uiBuilder = uiBuilder;
		this.mod = mod;
		this.modConfigs = configs;
		this.configIndex = index;
		this.currentTables = new IConfigTable[configs.size()];
	}
	
	@Override
	protected void init()
	{
		if (this.currentTables[this.configIndex] == null)
			this.currentTables[this.configIndex] = ForgeConfigTable.create(this.modConfigs.get(this.configIndex).<ForgeConfigSpec>getSpec().self(), this::onPropertyChanged);
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
			saveChanges();
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
			// TODO [#2] Get ui builder and style sheet from mod properties
			IConfigUIBuilder uiBuilder = BetterConfigBuilder::build;
			StyleSheet styleSheet;
			try
			{
				styleSheet = StyleSheet.findSheet(StyleSheet.DEFAULT_STYLESHEET);
			}
			catch (IOException e)
			{
                LOGGER.warn("Exception loading stylesheet: {}: {}", StyleSheet.DEFAULT_STYLESHEET, e);
				e.printStackTrace();
				return prevScreen;
			}
			BetterConfigScreen screen = new BetterConfigScreen(uiBuilder, styleSheet, mod, configs, 0);
			screen.setPrevScreen(prevScreen);
			return screen;
		};
	}
	
	@FunctionalInterface
	public static interface IConfigUIBuilder
	{
		IComponent build(BetterConfigScreen screen, IConfigTable config);
	}
}

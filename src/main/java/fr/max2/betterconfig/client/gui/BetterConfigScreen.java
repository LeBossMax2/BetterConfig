package fr.max2.betterconfig.client.gui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.better.BetterConfigBuilder;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.config.impl.value.ForgeConfigProperty;
import fr.max2.betterconfig.config.impl.value.ForgeConfigTable;
import fr.max2.betterconfig.config.value.IConfigTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class BetterConfigScreen extends Screen
{
	/** The config user interface builder */
	private final IConfigUIBuilder uiBuilder;
	/** The mod this configuration is from */
	private final ModContainer mod;
	
	/** The list of available configurations in the mod */
	private final List<ModConfig> modConfigs;
	/** The index of the current edited configuration in the list */
	private final int configIndex;
	/** The current edited configuration */
	private final ModConfig currentConfig;
	/** The current edited configuration table */
	private final IConfigTable currentTable;
	/** The set of properties that changed and need to be saved */
	private final Set<ForgeConfigProperty<?>> modifiedProperties = new HashSet<>();
	
	/** The screen to open when this screen closes */
	private Screen prevScreen = null;
	
	/** The current user interface */
	private IGuiComponent ui;
	
	private boolean autoSave = true;
	
	protected BetterConfigScreen(IConfigUIBuilder uiBuilder, ModContainer mod, List<ModConfig> configs, int index)
	{
		super(new StringTextComponent(mod.getModId() + " configuration : " + configs.get(index).getFileName()));
		this.uiBuilder = uiBuilder;
		this.mod = mod;
		this.modConfigs = configs;
		this.configIndex = index;
		this.currentConfig = this.modConfigs.get(this.configIndex);
		this.currentTable = ForgeConfigTable.create(this.currentConfig.getSpec(), this::onPropertyChanged);
	}
	
	@Override
	protected void init()
	{
		this.modifiedProperties.clear();
		// Builds the user interface
		this.ui = this.uiBuilder.build(this, this.currentTable);
		this.addListener(this.ui);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.ui.render(matrixStack, mouseX, mouseY, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.ui.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void closeScreen()
	{
		// Open back the previous screen
		this.minecraft.displayGuiScreen(this.prevScreen);
	}
	
	@Override
	public void onClose()
	{
		if (this.autoSave)
		{
			saveChanges();
		}
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
            this.currentConfig.save();
		}
	}
	
	public void cancelChanges()
	{
		if (this.configChanged())
		{
			this.autoSave = false;
			this.openConfig(this.configIndex);
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
		BetterConfigScreen newScreen = new BetterConfigScreen(this.uiBuilder, this.mod, this.modConfigs, index);
		newScreen.setPrevScreen(this.prevScreen);
		this.minecraft.displayGuiScreen(newScreen);
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
		return this.currentConfig;
	}

	/** Gets the font renderer of the screen */
	public FontRenderer getFont()
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
			// TODO [#2] Get ui builder and style from mod properties
			IConfigUIBuilder uiBuilder = BetterConfigBuilder::build;
			BetterConfigScreen screen = new BetterConfigScreen(uiBuilder, mod, configs, 0);
			screen.setPrevScreen(prevScreen);
			return screen;
		};
	}
	
	@FunctionalInterface
	public static interface IConfigUIBuilder
	{
		IGuiComponent build(BetterConfigScreen screen, IConfigTable config);
	}
}

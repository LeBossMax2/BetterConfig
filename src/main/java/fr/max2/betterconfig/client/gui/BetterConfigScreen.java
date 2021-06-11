package fr.max2.betterconfig.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.ConfigProperty;
import fr.max2.betterconfig.client.gui.builder.BetterConfigBuilder;
import fr.max2.betterconfig.client.gui.builder.IConfigUIBuilder;
import fr.max2.betterconfig.client.gui.builder.ITableUIBuilder;
import fr.max2.betterconfig.client.gui.builder.ValueType;
import fr.max2.betterconfig.client.gui.widget.IUIElement;
import fr.max2.betterconfig.client.gui.builder.IValueUIBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class BetterConfigScreen extends Screen
{
	private static final Logger LOGGER = LogManager.getLogger(BetterConfig.MODID);
	
	/** The config user interface builder */
	private final IConfigUIBuilder<? extends IUIElement> uiBuilder;
	/** The mod this configuration is from */
	private final ModContainer mod;
	
	/** The list of available configurations in the mod */
	private final List<ModConfig> modConfigs;
	/** The index of the current edited configuration in the list */
	private final int configIndex;
	/** The current edited configuration */
	private final ModConfig currentConfig;
	
	/** The screen to open when this screen closes */
	private Screen prevScreen = null;
	
	/** The current user interface */
	private IUIElement ui;
	
	protected BetterConfigScreen(IConfigUIBuilder<? extends IUIElement> uiBuilder, ModContainer mod, List<ModConfig> configs, int index)
	{
		super(new StringTextComponent(mod.getModId() + " configuration : " + configs.get(index).getFileName()));
		this.uiBuilder = uiBuilder;
		this.mod = mod;
		this.modConfigs = configs;
		this.configIndex = index;
		this.currentConfig = this.modConfigs.get(this.configIndex);
	}
	
	@Override
	protected void init()
	{
		// Builds the user interface
		this.ui = this.buildTableUI(this.uiBuilder.start(this), this.currentConfig.getSpec().getSpec(), this.currentConfig.getSpec().getValues());
		this.addListener(this.ui);
	}
	
	/**
	 * Builds the user interface for a config table
	 * @param <P> the type of user interface primitives
	 * @param builder the user interface builder
	 * @param spec the specification of the table
	 * @param values the values in the table
	 * @return the user interface primitive to draw the table
	 */
	protected <P> P buildTableUI(ITableUIBuilder<P> builder, UnmodifiableConfig spec, UnmodifiableConfig values)
	{
        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = values.valueMap();

    	List<P> tableContent = new ArrayList<>();

        for (Map.Entry<String, Object> specEntry : specMap.entrySet())
        {
            String key = specEntry.getKey();
            Object specValue = specEntry.getValue();
            Object configValue = configMap.get(key);

            if (specValue instanceof UnmodifiableConfig)
            {
            	String comment = ""; //TODO find a way to replace 'values.getComment(key);'
                tableContent.add(this.buildTableUI(builder.subTableBuilder(key, comment), (UnmodifiableConfig)specValue, (UnmodifiableConfig)configValue));
            }
            else
            {
                ValueSpec valueSpec = (ValueSpec)specValue;
                tableContent.add(this.buildValueIU(builder.tableEntryBuilder(key, valueSpec.getComment()), new ConfigProperty<>(valueSpec, (ConfigValue<?>)configValue)));
            }
        }
        
        return builder.buildTable(tableContent);
	}

	/**
	 * Builds the user interface for a config property
	 * @param <P> the type of user interface primitives
	 * @param builder the user interface builder
	 * @param property the configuration property
	 * @return the user interface primitive to draw the property
	 */
	protected <P> P buildValueIU(IValueUIBuilder<P> builder, ConfigProperty<?> property)
	{
		Class<?> specClass = property.getValueClass();
		ValueType type = ValueType.getType(specClass);
		
		if (type == null)
		{
			LOGGER.info("Configuration value of unknown type: " + specClass);
			return builder.buildUnknown(property);
		}

		return type.callBuilder(builder, property);
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
		if (this.configChanged())
		{
			// Save config and send config update event
            // TODO send config reloading event
            //this.currentConfig.fireEvent(new ModConfig.Reloading(this.currentConfig));
            this.currentConfig.save();
		}
	}
	
	/**
	 * Gets whether the configuration has been edited
	 * @return true if the configuration changed, false otherwise
	 */
	protected boolean configChanged()
	{
		// TODO only save when needed
		return true;
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
			// TODO get from mod properties
			IConfigUIBuilder<? extends IUIElement> uiBuilder = new BetterConfigBuilder();
			BetterConfigScreen screen = new BetterConfigScreen(uiBuilder, mod, configs, 0);
			screen.setPrevScreen(prevScreen);
			return screen;
		};
	}
}

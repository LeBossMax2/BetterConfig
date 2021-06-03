package fr.max2.betterconfig.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.builder.DebugBuilder;
import fr.max2.betterconfig.client.gui.builder.ConfigUIBuilder;
import fr.max2.betterconfig.client.gui.builder.TableUIBuilder;
import fr.max2.betterconfig.client.gui.builder.ValueType;
import fr.max2.betterconfig.client.gui.builder.ValueUIBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class BetterConfigScreen extends Screen
{
	public static final Logger LOGGER = LogManager.getLogger(BetterConfig.MODID);
	
	private final ConfigUIBuilder<? extends IUIElement> uiBuilder;
	private final ModContainer mod;
	
	private final List<ModConfig> modConfigs;
	private final int configIndex;
	private final ModConfig currentConfig;
	
	private Screen prevScreen;
	
	private IUIElement ui;
	
	protected BetterConfigScreen(ConfigUIBuilder<? extends IUIElement> uiBuilder, ModContainer mod, List<ModConfig> configs, int index)
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
		// TODO use result
		this.ui = this.buildTableUI(this.uiBuilder.start(this), this.currentConfig.getSpec().getSpec(), this.currentConfig.getConfigData());
		this.addListener(this.ui);
	}
	
	protected <P> P buildTableUI(TableUIBuilder<P> builder, UnmodifiableConfig spec, CommentedConfig config)
	{
        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();

    	List<P> tableContent = new ArrayList<>();

        for (Map.Entry<String, Object> specEntry : specMap.entrySet())
        {
            String key = specEntry.getKey();
            Object specValue = specEntry.getValue();
            Object configValue = configMap.get(key);

            if (specValue instanceof Config)
            {
            	String comment = config.getComment(key);
                if (!(configValue instanceof CommentedConfig))
                {
                	// Wrong value, replace the config with a new one
                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    configValue = newValue;
                }
                tableContent.add(this.buildTableUI(builder.subTableBuilder(key, comment), (Config)specValue, (CommentedConfig)configValue));
            }
            else
            {
                ValueSpec valueSpec = (ValueSpec)specValue;
                if (!valueSpec.test(configValue))
                {
                	// Wrong value, correct the value to a valid one
                    Object newValue = valueSpec.correct(configValue);
                    configMap.put(key, newValue);
                    configValue = newValue;
                }
                tableContent.add(this.buildValueIU(builder.tableEntryBuilder(key, valueSpec.getComment()), valueSpec, configValue));
            }
        }
        
        return builder.buildTable(tableContent);
	}

	protected <P> P buildValueIU(ValueUIBuilder<P> builder, ValueSpec spec, Object value)
	{
		Class<?> specClass = spec.getClazz();
		for (ValueType<?> type : ValueType.VALUE_TYPES)
		{
			if (type.matches(specClass))
			{
				return type.callBuilder(builder, spec, value);
			}
		}

		LOGGER.info("Configuration value of unknown type: " + specClass);
		return builder.buildUnknown(spec, value);
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
            this.currentConfig.getSpec().afterReload();
            // TODO send config reloading event
            //this.currentConfig.fireEvent(new ModConfig.Reloading(this.currentConfig));
            this.currentConfig.save();
		}
	}
	
	protected boolean configChanged()
	{
		return true;
	}
	
	public void setPrevScreen(Screen prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	public ModContainer getMod()
	{
		return mod;
	}
	
	
	public List<ModConfig> getModConfigs()
	{
		return modConfigs;
	}
	
	public int getCurrentConfigIndex()
	{
		return configIndex;
	}
	
	public ModConfig getCurrentConfig()
	{
		return currentConfig;
	}
	
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
			ConfigUIBuilder<? extends IUIElement> uiBuilder = new DebugBuilder();
			BetterConfigScreen screen = new BetterConfigScreen(uiBuilder, mod, configs, 0);
			screen.setPrevScreen(prevScreen);
			return screen;
		};
	}
}

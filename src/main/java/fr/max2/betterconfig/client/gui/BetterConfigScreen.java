package fr.max2.betterconfig.client.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class BetterConfigScreen extends Screen
{
	private final Screen prevScreen;
	private final ModContainer mod;
	private final List<ModConfig> modConfigs;
	private final int configIndex;
	private final ModConfig currentConfig;
	
	protected BetterConfigScreen(Minecraft mc, Screen prevScreen, ModContainer mod, List<ModConfig> configs, int index)
	{
		super(new StringTextComponent(mod.getModId() + " configuration : " + configs.get(index).getFileName()));
		this.prevScreen = prevScreen;
		this.mod = mod;
		this.modConfigs = configs;
		this.configIndex = index;
		this.currentConfig = this.modConfigs.get(this.configIndex);
	}
	
	@Override
	protected void init()
	{
		LinkedList<String> parentPath = new LinkedList<>(); //Linked list for fast add/removes
		this.loadConfigInterface(this.currentConfig.getSpec().getSpec(), this.currentConfig.getConfigData(), parentPath, Collections.unmodifiableList(parentPath));
	}
	
	private void loadConfigInterface(UnmodifiableConfig spec, CommentedConfig config, LinkedList<String> parentPath, List<String> parentPathUnmodifiable)
	{
        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();

        for (Map.Entry<String, Object> specEntry : specMap.entrySet())
        {
            String key = specEntry.getKey();
            Object specValue = specEntry.getValue();
            Object configValue = configMap.get(key);

            parentPath.addLast(key);

            if (specValue instanceof Config)
            {
            	String comment = config.getComment(key);
            	this.startSection(parentPathUnmodifiable, comment);
                if (configValue instanceof CommentedConfig)
                {
                	this.loadConfigInterface((Config)specValue, (CommentedConfig)configValue, parentPath, parentPathUnmodifiable);
                }
                else
                {
                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    configValue = newValue;
                    this.loadConfigInterface((Config)specValue, newValue, parentPath, parentPathUnmodifiable);
                }
            	this.endSection(parentPathUnmodifiable);
            }
            else
            {
                ValueSpec valueSpec = (ValueSpec)specValue;
                if (!valueSpec.test(configValue))
                {
                    Object newValue = valueSpec.correct(configValue);
                    configMap.put(key, newValue);
                    configValue = newValue;
                }
                this.addValueWidget(parentPathUnmodifiable, valueSpec, configValue);
            }

            parentPath.removeLast();
        }
	}
	
	protected void startSection(List<String> sectionPath, String comment)
	{
		
	}
	protected void endSection(List<String> sectionPath)
	{
		
	}
	protected void addValueWidget(List<String> valuePath, ValueSpec valueSpec, Object value)
	{
		
	}
	protected boolean configChanged()
	{
		return true;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.font.drawString(matrixStack, "Better Config !", this.width / 2, this.height / 2, 0xFFFFFFFF);
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
	
	/**
	 * Gets a factory creating a configuration GUI for the given mod
	 * @param mod
	 * @return a function to build the GUI
	 */
	public static BiFunction<Minecraft, Screen, Screen> factory(ModContainer mod, List<ModConfig> configs)
	{
		ConfigGuiFactory factory = BetterConfigScreen::new; // TODO get from mod properties
		return (mc, prevScreen) -> factory.make(mc, prevScreen, mod, configs, 0);
	}
	
	public static interface ConfigGuiFactory
	{
		BetterConfigScreen make(Minecraft mc, Screen prevScreen, ModContainer mod, List<ModConfig> configs, int index);
	}
}

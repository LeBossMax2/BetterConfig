package fr.max2.betterconfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.ConfigScreenHandler;

@EventBusSubscriber(modid = BetterConfig.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ConfigExtension
{
	private static final Logger LOGGER = LogManager.getLogger(BetterConfig.MODID);

	@SubscribeEvent
	public static void registerExtensions(FMLClientSetupEvent event)
	{
		ModList.get().forEachModContainer(ConfigExtension::setupConfigExtensionPoint);
	}

	/**
	 * Configures the ConfigScreenHandler.ConfigScreenFactory extension point of the given mod to the custom config screen
	 * @param modId the id of the mod
	 * @param mod the container of the mod
	 */
	private static void setupConfigExtensionPoint(String modId, ModContainer mod)
	{
		if (mod.getCustomExtension(ConfigScreenHandler.ConfigScreenFactory.class).isPresent())
			return; // No custom config screen if one already registered

		List<ModConfig> configs = getModConfigs(mod);
		if (configs.isEmpty())
			return; // No config screen if the mod has no config

		LOGGER.debug("Registering extension point for " + modId);
		mod.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(BetterConfigScreen.factory(mod, configs)));
	}

	/** Gets the list of modifiable configurations for the given mod */
	private static List<ModConfig> getModConfigs(ModContainer mod)
	{
		Map<ModConfig.Type, ModConfig> configMap = ObfuscationReflectionHelper.getPrivateValue(ModContainer.class, mod, "configs");

		if (configMap == null)
			return Collections.emptyList();

		return configMap.values().stream().filter(ConfigExtension::isConfigEditable).collect(Collectors.toList());
	}

	/** Checks if the given config can be edited */
	private static boolean isConfigEditable(ModConfig config)
	{
		// TODO [#3, #4] Allow server config to be changed
		return config.getConfigData() != null;
	}
}

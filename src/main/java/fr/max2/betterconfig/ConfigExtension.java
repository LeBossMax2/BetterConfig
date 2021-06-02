package fr.max2.betterconfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = BetterConfig.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ConfigExtension
{
	public static final Logger LOGGER = LogManager.getLogger(BetterConfig.MODID);
	
	@SubscribeEvent
	public static void registerExtensions(FMLClientSetupEvent event)
	{
		ModList.get().forEachModContainer(ConfigExtension::setupConfigExtensionPoint);
	}
	
	/**
	 * Configures the CONFIGGUIFACTORY extension point of the given mod to the custom gui config
	 * @param modId the id of the mod
	 * @param mod the container of the mod
	 */
	private static void setupConfigExtensionPoint(String modId, ModContainer mod)
	{
		if (mod.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY).isPresent())
			return;

		Collection<ModConfig> configs = getModConfigs(mod);
		if (configs.isEmpty())
			return;
		
		LOGGER.debug("Registering extension point for " + modId);
		mod.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> BetterConfigScreen.factory(mod, new ArrayList<>(configs)));
	}

	@SuppressWarnings("unchecked")
	private static Collection<ModConfig> getModConfigs(ModContainer mod)
	{
		return ((Map<ModConfig.Type, ModConfig>)ObfuscationReflectionHelper.getPrivateValue(ModContainer.class, mod, "configs")).values();
	}
}

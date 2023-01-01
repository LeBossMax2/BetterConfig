package fr.max2.betterconfig.client.gui.style;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(value = Dist.CLIENT, modid = BetterConfig.MODID, bus = Bus.MOD)
public enum StyleSheetManager implements ResourceManagerReloadListener
{
	INSTANCE;

	public static Gson GSON = StyleSerializer.INSTANCE.registerSerializers(new GsonBuilder()).create();

	private Map<ResourceLocation, StyleSheet> cache = new HashMap<>();

	public StyleSheet getStyleSheet(ResourceLocation sheetLocation) throws IOException
	{
		StyleSheet sheet = this.cache.get(sheetLocation);
		if (sheet != null)
			return sheet;

		ResourceLocation resourceLocation = new ResourceLocation(sheetLocation.getNamespace(), StyleSheet.STYLESHEET_DIR + "/" + sheetLocation.getPath() + ".json");
		Resource res = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).orElse(null);
		if (res == null)
			return null;
		try (InputStream inputStream = res.open())
		{
			sheet = GsonHelper.fromJson(GSON, new InputStreamReader(inputStream, StandardCharsets.UTF_8), StyleSheet.Builder.class).build();
		}

		this.cache.put(sheetLocation, sheet);
		return sheet;
	}

	@Override
	public void onResourceManagerReload(ResourceManager pResourceManager)
	{
		this.cache.clear();
	}

	@SubscribeEvent
	public static void registerReloadListener(RegisterClientReloadListenersEvent event)
	{
		event.registerReloadListener(INSTANCE);
	}

}

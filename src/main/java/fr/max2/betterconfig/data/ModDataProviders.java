package fr.max2.betterconfig.data;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = BetterConfig.MODID, bus = Bus.MOD)
public class ModDataProviders
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        
        if (event.includeClient())
        {
            gen.addProvider(new ModLanguagesProvider(gen));
            gen.addProvider(new ModStyleSheetProvider(gen));
        }
    }
}

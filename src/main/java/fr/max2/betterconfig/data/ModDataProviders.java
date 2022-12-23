package fr.max2.betterconfig.data;

import fr.max2.betterconfig.BetterConfig;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = BetterConfig.MODID, bus = Bus.MOD)
public class ModDataProviders
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new ModLanguagesProvider(output));
        gen.addProvider(event.includeClient(), new ModStyleSheetProvider(output));
    }
}

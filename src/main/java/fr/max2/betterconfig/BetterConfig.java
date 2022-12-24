package fr.max2.betterconfig;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;

@Mod(BetterConfig.MODID)
public class BetterConfig
{
	public static final String MODID = "betterconfig";
	
	
	public BetterConfig()
	{
		// Marks this mod as one-sided
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		
		// Register testing config when in development environment
		if(!FMLLoader.isProduction())
			TestConfig.registerConfigs();
	}
}

package fr.max2.betterconfig;

import org.apache.commons.lang3.tuple.Pair;

import fr.max2.betterconfig.client.gui.builder.ValueType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class TestConfig
{
	private static final ForgeConfigSpec CLIENT_SPEC;
	private static final Client CLIENT;
	private static final ForgeConfigSpec COMMON_SPEC;
	private static final Common COMMON;
	
	static
	{
		Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT = clientPair.getLeft();
		CLIENT_SPEC = clientPair.getRight();
		Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonPair.getLeft();
		COMMON_SPEC = commonPair.getRight();
	}
	
	static void registerConfigs()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
	}
	
	private static class Client
	{
		public Client(ForgeConfigSpec.Builder spec)
		{
			spec.comment("This is a boolean")
				.define("myBool", false);
			
			spec.comment("This is a string")
				.define("myString", "DefaultString", obj ->
				{
					if (!(obj instanceof String))
						return false;
					String str = (String)obj;
					return str.length() > 4;
				});
			
			spec.comment("This is an enum")
				.translation("mod.my.enum")
				.defineEnum("myEnum", ValueType.ENUM);
			
			spec.comment("This is an interger")
				.defineInRange("myInt", 5, 0, 10);
			
			spec.comment("This is a double")
				.defineInRange("myDouble", 5.0, 0.0, 100.0);
		}
	}
	
	private static class Common
	{
		public Common(ForgeConfigSpec.Builder spec)
		{
			spec.comment("This is the first section")
            	.push("section1");
			
			spec.define("bool1", false);

			spec.pop();
			
			spec.comment("This is the second section")
	        	.push("section2");
			
			spec.define("bool2", false);

			
			spec.comment("This is the third section")
	        	.push("section3");
			
			spec.define("bool3", false);
	
			spec.pop();
	
			spec.pop();
		}
	}
}

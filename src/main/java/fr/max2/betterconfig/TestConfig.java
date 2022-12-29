package fr.max2.betterconfig;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class TestConfig
{
	private static final ForgeConfigSpec CLIENT_SPEC;
	private static final ForgeConfigSpec COMMON_SPEC;

	static
	{
		Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = clientPair.getRight();
		Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
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
				.defineEnum("myEnum", Dist.CLIENT);

			spec.comment("This is an interger")
				.defineInRange("myInt", 5, 0, 10);

			spec.comment("This is a double")
				.defineInRange("myDouble", 5.0, 0.0, 100.0);

			spec.comment("This is a list")
				.defineList("myDoubles", Arrays.asList(0.0), val ->
				{
					if (!(val instanceof Double))
						return false;

					return true;
				});

			spec.defineList("myListsOfStrings", Arrays.asList(Arrays.asList("Exemple")), val ->
				{
					if (!(val instanceof List))
						return false;

					List<?> v = (List<?>)val;

					return v.stream().allMatch(elem -> elem instanceof String);
				});
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

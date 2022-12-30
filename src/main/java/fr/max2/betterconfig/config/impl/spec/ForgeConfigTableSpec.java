package fr.max2.betterconfig.config.impl.spec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import fr.max2.betterconfig.config.ConfigTableKey;
import fr.max2.betterconfig.config.ConfigLocation;
import fr.max2.betterconfig.config.PrimitiveType;
import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeConfigTableSpec
{
	private static ConfigTableSpec newForgeConfigTableSpec(ConfigLocation loc, UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		ImmutableList.Builder<ConfigTableSpec.Entry> builder = ImmutableList.builder();
		for (Map.Entry<String, Object> entry : spec.valueMap().entrySet())
		{
			builder.add(childNode(entry.getKey(), entry.getValue(), loc, levelComments));
		}
		return new ConfigTableSpec(builder.build());
	}

	public static ConfigTableSpec newForgeConfigTableSpec(UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		return newForgeConfigTableSpec(ConfigLocation.ROOT, spec, levelComments);
	}

	private static ConfigTableSpec.Entry childNode(String key, Object spec, ConfigLocation tableLoc, Function<ConfigLocation, String> levelComments)
	{
		ConfigLocation location = new ConfigLocation(tableLoc, key);
		if (spec instanceof UnmodifiableConfig)
        {
            Component name = Component.literal(location.getName()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
        	return new ConfigTableSpec.Entry(new ConfigTableKey(location.getName(), name, levelComments.apply(location)), ForgeConfigTableSpec.newForgeConfigTableSpec(location, (UnmodifiableConfig)spec, levelComments));
        }

        ValueSpec forgeSpec = (ValueSpec)spec;

        Component name = null;
        // Try getting name from translation key
		String translationKey = forgeSpec.getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey) && Language.getInstance().has(translationKey))
			name = Component.translatable(translationKey);
		else // Get name from path
			name = Component.literal(key);

		ConfigSpec valSpec = makeSpec(forgeSpec, valueClass(forgeSpec));

        return new ConfigTableSpec.Entry(new ConfigTableKey(location.getName(), name, forgeSpec.getComment()), valSpec);
	}

	private static <T> ConfigSpec makeSpec(ValueSpec forgeSpec, Class<T> valueClass)
	{
		if (List.class.isAssignableFrom(valueClass))
		{
			return new ConfigListSpec(makeListChildSpec((List<?>)forgeSpec.getDefault()));
		}

		var type = PrimitiveType.getType(valueClass);
		if (type != null)
		{
			return type.makeSpec(new ForgeConfigPrimitiveSpec<>(forgeSpec, valueClass));
		}

		return new ConfigUnknownSpec(new ForgeUnknownSpec(forgeSpec, valueClass));
	}

	private static Class<?> valueClass(ValueSpec spec)
	{
		Class<?> specClass = spec.getClazz();
		if (specClass != Object.class)
			return specClass;

		Object value = spec.getDefault();
		if (value != null)
			return value.getClass();

		return Object.class;
	}

	private static ConfigSpec makeListChildSpec(List<?> exampleValues)
	{
		for (Object obj : exampleValues)
		{
			if (obj == null)
				continue;

			var spec = makeChildSpec(obj, obj.getClass());
			if (spec != null)
				return spec;
		}
		return new ConfigUnknownSpec(new ForgeUnknownSpec(null, Object.class));
	}

	private static <T> ConfigSpec makeChildSpec(Object obj, Class<T> valClass)
	{
		if (List.class.isAssignableFrom(valClass))
		{
			return new ConfigListSpec(makeListChildSpec((List<?>)obj));
		}

		if (UnmodifiableConfig.class.isAssignableFrom(valClass))
		{
			// TODO [#5] Implement list of tables
			// Don't know how to deal with list of tables
			return new ConfigUnknownSpec(new ForgeUnknownSpec(null, valClass));
		}

		var type = PrimitiveType.getType(valClass);
		if (type != null)
		{
			return type.makeSpec(new ForgeListPrimitiveSpec<>(valClass));
		}

		return null;
	}
}

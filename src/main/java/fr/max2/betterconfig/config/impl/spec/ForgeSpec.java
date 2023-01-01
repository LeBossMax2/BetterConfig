package fr.max2.betterconfig.config.impl.spec;

import java.util.List;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import fr.max2.betterconfig.config.ConfigTableKey;
import fr.max2.betterconfig.config.PrimitiveType;
import fr.max2.betterconfig.config.impl.ConfigLocation;
import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigUnknownSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeSpec
{
	public static ConfigTableSpec makeSpec(UnmodifiableConfig forgeSpec, Function<ConfigLocation, String> levelComments)
	{
		return makeTableSpecNode(ConfigLocation.ROOT, forgeSpec, levelComments);
	}

	private static ConfigTableSpec makeTableSpecNode(ConfigLocation loc, UnmodifiableConfig forgeSpec, Function<ConfigLocation, String> levelComments)
	{
		ImmutableList.Builder<ConfigTableSpec.Entry> builder = ImmutableList.builder();
		for (var entry : forgeSpec.valueMap().entrySet())
		{
			builder.add(makeTableSpecEntry(entry.getKey(), entry.getValue(), loc, levelComments));
		}
		return new ConfigTableSpec(builder.build());
	}

	private static ConfigTableSpec.Entry makeTableSpecEntry(String key, Object forgeSpecObject, ConfigLocation tableLoc, Function<ConfigLocation, String> levelComments)
	{
		if (forgeSpecObject instanceof UnmodifiableConfig forgeSpecTable)
        {
			// TODO use level translation key (ForgeConfigSpec#getLevelTranslationKey)
			var location = new ConfigLocation(tableLoc, key);
            var name = Component.literal(key).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);

            var tableSpec = ForgeSpec.makeTableSpecNode(location, forgeSpecTable, levelComments);
        	return new ConfigTableSpec.Entry(new ConfigTableKey(key, name, levelComments.apply(location)), tableSpec);
        }
		else if (forgeSpecObject instanceof ValueSpec forgeSpec)
		{
			Component name;
			// Try getting name from translation key
			var translationKey = forgeSpec.getTranslationKey();
			if (!Strings.isNullOrEmpty(translationKey) && Language.getInstance().has(translationKey))
			{
				name = Component.translatable(translationKey);
			}
			else // Get name from path
			{
				name = Component.literal(key);
			}

			var valSpec = makeValueSpecNode(forgeSpec, getValueClass(forgeSpec));
			return new ConfigTableSpec.Entry(new ConfigTableKey(key, name, forgeSpec.getComment()), valSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private static <T> ConfigSpec makeValueSpecNode(ValueSpec forgeSpec, Class<T> valueClass)
	{
		if (List.class.isAssignableFrom(valueClass))
		{
			return new ConfigListSpec(makeSpecNodeFromList((List<?>)forgeSpec.getDefault()));
		}

		var type = PrimitiveType.getType(valueClass);
		if (type != null)
		{
			return type.makeSpec(new ForgeConfigPrimitiveSpec<>(forgeSpec, valueClass));
		}

		return new ConfigUnknownSpec(new ForgeUnknownSpec(forgeSpec, valueClass));
	}

	private static ConfigSpec makeSpecNodeFromList(List<?> exampleValues)
	{
		for (var obj : exampleValues)
		{
			if (obj == null)
				continue;

			var spec = makeSpecNodeFromObject(obj, obj.getClass());
			if (spec != null)
				return spec;
		}
		return new ConfigUnknownSpec(new ForgeUnknownSpec(null, Object.class));
	}

	private static <T> ConfigSpec makeSpecNodeFromObject(Object obj, Class<T> valClass)
	{
		if (List.class.isAssignableFrom(valClass))
		{
			return new ConfigListSpec(makeSpecNodeFromList((List<?>)obj));
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

	private static Class<?> getValueClass(ValueSpec forgeSpec)
	{
		var specClass = forgeSpec.getClazz();
		if (specClass != Object.class)
			return specClass;

		var value = forgeSpec.getDefault();
		if (value != null)
			return value.getClass();

		return Object.class;
	}
}

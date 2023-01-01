package fr.max2.betterconfig.config.impl.spec;

import java.util.List;

import org.jetbrains.annotations.Nullable;

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
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeSpec
{
	public static ConfigTableSpec makeSpec(ForgeConfigSpec forgeSpec)
	{
		return makeTableSpecNode(ConfigLocation.ROOT, forgeSpec, new TableInfoProvider(forgeSpec));
	}

	private static ConfigTableSpec makeTableSpecNode(ConfigLocation loc, UnmodifiableConfig forgeSpec, TableInfoProvider tableInfo)
	{
		ImmutableList.Builder<ConfigTableSpec.Entry> builder = ImmutableList.builder();
		for (var entry : forgeSpec.valueMap().entrySet())
		{
			builder.add(makeTableSpecEntry(entry.getKey(), entry.getValue(), loc, tableInfo));
		}
		return new ConfigTableSpec(builder.build());
	}

	private static ConfigTableSpec.Entry makeTableSpecEntry(String key, Object forgeSpecObject, ConfigLocation tableLoc, TableInfoProvider tableInfo)
	{
		if (forgeSpecObject instanceof UnmodifiableConfig forgeSpecTable)
        {
			var location = new ConfigLocation(tableLoc, key);
			var name =
				getDisplayName(tableInfo.getLevelTranslationKey(location), key)
					.withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);

            var tableSpec = ForgeSpec.makeTableSpecNode(location, forgeSpecTable, tableInfo);
        	return new ConfigTableSpec.Entry(new ConfigTableKey(key, name, tableInfo.getLevelComment(location)), tableSpec);
        }
		else if (forgeSpecObject instanceof ValueSpec forgeSpec)
		{
			var name = getDisplayName(forgeSpec.getTranslationKey(), key);

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

	private static MutableComponent getDisplayName(@Nullable String translationKey, String identifier)
	{
		// Try getting name from translation key
		if (!Strings.isNullOrEmpty(translationKey) && Language.getInstance().has(translationKey))
		{
			return Component.translatable(translationKey);
		}
		else
		{
			// Get name from identifier
			return Component.literal(identifier);
		}
	}

	private static class TableInfoProvider
	{
		private final ForgeConfigSpec forgeSpec;

		public TableInfoProvider(ForgeConfigSpec forgeSpec)
		{
			this.forgeSpec = forgeSpec;
		}

		/** Gets the comments from the spec */
		String getLevelComment(ConfigLocation loc)
		{
			return this.forgeSpec.getLevelComment(loc.getPath());
		}

		/** Gets the translation keys from the spec */
		String getLevelTranslationKey(ConfigLocation loc)
		{
			return this.forgeSpec.getLevelTranslationKey(loc.getPath());
		}
	}
}

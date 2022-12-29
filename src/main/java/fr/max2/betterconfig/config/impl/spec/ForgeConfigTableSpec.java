package fr.max2.betterconfig.config.impl.spec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import fr.max2.betterconfig.config.ConfigIdentifier;
import fr.max2.betterconfig.config.ConfigLocation;
import fr.max2.betterconfig.config.ValueType;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeConfigTableSpec implements IConfigTableSpec
{
	private final ConfigLocation tableLoc;
	/** The map containing the comment for each node */
	private final Function<ConfigLocation, String> levelComments;
	/** The table containing the specification of each entry */
	private final UnmodifiableConfig spec;

	private final List<IConfigTableSpec.Entry> entrySpecs;

	private ForgeConfigTableSpec(ConfigLocation loc, UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		this.levelComments = levelComments;
		this.spec = spec;
		this.tableLoc = loc;
		ImmutableList.Builder<IConfigTableSpec.Entry> builder = ImmutableList.builder();
		for (Map.Entry<String, Object> entry : this.spec.valueMap().entrySet())
		{
			builder.add(this.childNode(entry.getKey(), entry.getValue()));
		}
		this.entrySpecs = builder.build();
	}

	public ForgeConfigTableSpec(UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		this(ConfigLocation.ROOT, spec, levelComments);
	}

	@Override
	public List<IConfigTableSpec.Entry> getEntrySpecs()
	{
		return this.entrySpecs;
	}

	private IConfigTableSpec.Entry childNode(String key, Object spec)
	{
		ConfigLocation location = new ConfigLocation(this.tableLoc, key);
		if (spec instanceof UnmodifiableConfig)
        {
            Component name = Component.literal(location.getName()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
        	return new IConfigTableSpec.Entry(new ConfigIdentifier(location, name, this.levelComments.apply(location)), new ConfigSpec.Table(new ForgeConfigTableSpec(location, (UnmodifiableConfig)spec, this.levelComments)));
        }

        ValueSpec forgeSpec = (ValueSpec)spec;

        Component name = null;
        // Try getting name from translation key
		String translationKey = forgeSpec.getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey) && Language.getInstance().has(translationKey))
			name = Component.translatable(translationKey);
		else // Get name from path
			name = Component.literal(key);

		ConfigSpec valSpec;
		Class<?> valueClass = valueClass(forgeSpec);

		if (List.class.isAssignableFrom(valueClass))
		{
			valSpec = new ConfigSpec.List(new ForgeConfigListSpec(getSpecForValues((List<?>)forgeSpec.getDefault())));
		}
		else
		{
			ValueType type = ValueType.getType(valueClass);
			if (type == null)
			{
				valSpec = new ConfigSpec.Unknown(new ForgeUnknownSpec(forgeSpec, valueClass));
			}
			else
			{
				valSpec = type.makeSpec(new ForgeConfigPrimitiveSpec<>(forgeSpec, valueClass));
			}
		}

        return new IConfigTableSpec.Entry(new ConfigIdentifier(location, name, forgeSpec.getComment()), valSpec);
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

	private static ConfigSpec getSpecForValues(List<?> exampleValues)
	{
		for (Object obj : exampleValues)
		{
			if (obj == null)
				continue;

			Class<?> valClass = obj.getClass();
			if (List.class.isAssignableFrom(valClass))
			{
				return new ConfigSpec.List(new ForgeConfigListSpec(getSpecForValues((List<?>)obj)));
			}
			if (UnmodifiableConfig.class.isAssignableFrom(valClass))
			{
				// TODO [#5] Implement list of tables
				// Don't know how to deal with list of tables
				return new ConfigSpec.Unknown(new ForgeListPrimitiveSpec<>(Object.class));
			}
			return ConfigSpec.Primitive.make(new ForgeListPrimitiveSpec<>(valClass));
		}
		return new ConfigSpec.Unknown(new ForgeListPrimitiveSpec<>(Object.class));
	}
}

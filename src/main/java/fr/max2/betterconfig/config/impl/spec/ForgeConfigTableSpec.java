package fr.max2.betterconfig.config.impl.spec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import fr.max2.betterconfig.config.spec.ConfigLocation;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ForgeConfigTableSpec implements IConfigTableSpec
{
	private final ConfigLocation tableLoc;
	/** The map containing the comment for each node */
	private final Function<ConfigLocation, String> levelComments;
	/** The table containing the specification of each entry */
	private final UnmodifiableConfig spec;
	
	private final List<ConfigTableEntrySpec> entrySpecs;
	
	private ForgeConfigTableSpec(ConfigLocation loc, UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		this.levelComments = levelComments;
		this.spec = spec;
		this.tableLoc = loc;
		ImmutableList.Builder<ConfigTableEntrySpec> builder = ImmutableList.builder();
		for (Map.Entry<String, Object> entry : this.spec.valueMap().entrySet())
		{
			builder.add(childNode(entry.getKey(), entry.getValue()));
		}
		this.entrySpecs = builder.build();
	}
	
	public ForgeConfigTableSpec(UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		this(ConfigLocation.ROOT, spec, levelComments);
	}
	
	@Override
	public UnmodifiableConfig getDefaultValue()
	{
		throw new UnsupportedOperationException(); // TODO [#5] Implement default value for tables
	}
	
	@Override
	public List<ConfigTableEntrySpec> getEntrySpecs()
	{
		return this.entrySpecs;
	}
	
	private ConfigTableEntrySpec childNode(String key, Object spec)
	{
		ConfigLocation location = new ConfigLocation(this.tableLoc, key);
		if (spec instanceof UnmodifiableConfig)
        {
            Component name = new TextComponent(location.getName()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
        	return new ConfigTableEntrySpec(location, new ForgeConfigTableSpec(location, (UnmodifiableConfig)spec, this.levelComments), name, this.levelComments.apply(location));
        }
		
        ValueSpec forgeSpec = (ValueSpec)spec;
        
        Component name = null;
        // Try getting name from translation key 
		String translationKey = forgeSpec.getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey) && Language.getInstance().has(translationKey))
			name = new TranslatableComponent(translationKey);
		else // Get name from path
			name = new TextComponent(key);
		
		IConfigSpecNode valSpec;
		Class<?> valueClass = valueClass(forgeSpec);
		
		if (List.class.isAssignableFrom(valueClass))
		{
			valSpec = new ForgeConfigListSpec<>(getSpecForValues((List<?>)forgeSpec.getDefault()));
		}
		else
		{
			valSpec = new ForgeConfigPrimitiveSpec<>(forgeSpec, valueClass);
		}
        
        return new ConfigTableEntrySpec(location, valSpec, name, forgeSpec.getComment());
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
	
	private static IConfigSpecNode getSpecForValues(List<?> exampleValues)
	{
		for (Object obj : exampleValues)
		{
			if (obj == null)
				continue;
			
			Class<?> valClass = obj.getClass();
			if (List.class.isAssignableFrom(valClass))
			{
				return new ForgeConfigListSpec<>(getSpecForValues((List<?>)obj));
			}
			if (UnmodifiableConfig.class.isAssignableFrom(valClass))
			{
				// TODO [#5] Implement list of tables
				// Don't know how to deal with list of tables
				return new ForgeListPrimitiveSpec<>(Object.class);
			}
			return new ForgeListPrimitiveSpec<>(valClass);
		}
		return new ForgeListPrimitiveSpec<>(Object.class);
	}
}

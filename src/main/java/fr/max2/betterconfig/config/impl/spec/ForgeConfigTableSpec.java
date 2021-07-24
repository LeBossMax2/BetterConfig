package fr.max2.betterconfig.config.impl.spec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Strings;

import fr.max2.betterconfig.config.spec.ConfigLocation;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.util.MappedMapView;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ForgeConfigTableSpec implements IConfigTableSpec
{
	private final ConfigLocation tableLoc;
	/** The map containing the comment for each node */
	private final Function<ConfigLocation, String> levelComments;
	/** The table containing the specification of each entry */
	private final UnmodifiableConfig spec;
	
	private Map<String, ConfigTableEntrySpec> specMap;
	
	private ForgeConfigTableSpec(ConfigLocation loc, UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		this.levelComments = levelComments;
		this.spec = spec;
		this.tableLoc = loc;
	}
	
	public ForgeConfigTableSpec(ForgeConfigSpec spec)
	{
		this(ConfigLocation.ROOT, spec.getSpec(), getSpecComments(spec));
	}
	
	/** Gets the comments from the spec */
	private static Function<ConfigLocation, String> getSpecComments(ForgeConfigSpec spec)
	{
		Map<List<String>, String> map = ObfuscationReflectionHelper.getPrivateValue(ForgeConfigSpec.class, spec, "levelComments");
		return loc -> map.get(loc.getPath());
	}
	
	@Override
	public Map<String, ConfigTableEntrySpec> getSpecMap()
	{
		if (this.specMap == null)
		{
			this.specMap = new MappedMapView<>(this.spec.valueMap(), (key, spec) -> childNode(key, spec));
		}
		return this.specMap;
	}
	
	private ConfigTableEntrySpec childNode(String key, Object spec)
	{
		ConfigLocation location = new ConfigLocation(this.tableLoc, key);
		if (spec instanceof UnmodifiableConfig)
        {
            IFormattableTextComponent name = new StringTextComponent(location.getName()).mergeStyle(TextFormatting.BOLD, TextFormatting.YELLOW);
        	return new ConfigTableEntrySpec(location, new ForgeConfigTableSpec(location, (UnmodifiableConfig)spec, this.levelComments), name, this.levelComments.apply(location));
        }
        else
        {
            ValueSpec forgeSpec = (ValueSpec)spec;
            
            IFormattableTextComponent name = null;
            // Try getting name from translation key 
    		String translationKey = forgeSpec.getTranslationKey();
    		if (!Strings.isNullOrEmpty(translationKey) && LanguageMap.getInstance().func_230506_b_(translationKey)) // func_230506_b_ is equivalent to "I18n.hasKey"
    			name = new TranslationTextComponent(translationKey);
    		else // Get name from path
    			name = new StringTextComponent(key);
    		
    		IConfigSpecNode valSpec;
    		Class<?> valueClass = valueClass(forgeSpec);
    		
    		if (List.class.isAssignableFrom(valueClass))
    		{
        		name.mergeStyle(TextFormatting.BOLD, TextFormatting.YELLOW);
    			valSpec = new ForgeConfigListSpec(getSpecForValues((List<?>)forgeSpec.getDefault()));
    		}
    		else
    		{
    			valSpec = new ForgeConfigPrimitiveSpec<>(forgeSpec, valueClass);
    		}
            
            return new ConfigTableEntrySpec(location, valSpec, name, forgeSpec.getComment());
        }
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
				return new ForgeConfigListSpec(getSpecForValues((List<?>)obj));
			}
			if (UnmodifiableConfig.class.isAssignableFrom(valClass))
			{
				// TODO [2.0] Implements list of tables
				// Don't know how to deal with tables in lists
				return new ForgeListPrimitiveSpec<>(Object.class);
			}
			return new ForgeListPrimitiveSpec<>(valClass);
		}
		return new ForgeListPrimitiveSpec<>(Object.class);
	}
}

package fr.max2.betterconfig.config.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Strings;

import fr.max2.betterconfig.config.spec.ConfigLocation;
import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import fr.max2.betterconfig.util.MappedMapView;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ForgeConfigTableSpec extends ConfigTableSpec
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
        	return new ConfigTableEntrySpec(location, new ForgeConfigTableSpec(location, (UnmodifiableConfig)spec, this.levelComments), new StringTextComponent(key), this.levelComments.apply(location));
        }
        else
        {
            ValueSpec valueSpec = (ValueSpec)spec;
            
            ITextComponent name = null;
            // Try getting name from translation key 
    		String translationKey = valueSpec.getTranslationKey();
    		if (!Strings.isNullOrEmpty(translationKey))
    			name = new TranslationTextComponent(translationKey);
    		else // Get name from path
    			name = new StringTextComponent(key);
            
            return new ConfigTableEntrySpec(location, new ForgeConfigPropertySpec<>(valueSpec), name, valueSpec.getComment());
        }
	}
}

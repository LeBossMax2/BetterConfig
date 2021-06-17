package fr.max2.betterconfig.config.spec;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ConfigTableSpec extends ConfigSpecNode
{
	/** The map containing the comment for each node */
	private final Function<ConfigLocation, String> levelComments;
	/** The table containing the specification of each entry */
	private final UnmodifiableConfig spec;
	/** The comment associated with the table */
	private final String comment;
	
	private Map<String, ConfigSpecNode> specMap;
	
	private ConfigTableSpec(ConfigLocation loc, UnmodifiableConfig spec, Function<ConfigLocation, String> levelComments)
	{
		super(loc);
		this.levelComments = levelComments;
		this.spec = spec;
		this.comment = levelComments.apply(this.getLoc());
	}
	
	public ConfigTableSpec(ForgeConfigSpec spec)
	{
		this(ConfigLocation.ROOT, spec.getSpec(), getSpecComments(spec));
	}
	
	@Override
	public String getCommentString()
	{
		return this.comment;
	}
	
	// TODO explore functions
	
	/** Gets the comments from the spec */
	private static Function<ConfigLocation, String> getSpecComments(ForgeConfigSpec spec)
	{
		Map<List<String>, String> map = ObfuscationReflectionHelper.getPrivateValue(ForgeConfigSpec.class, spec, "levelComments");
		return loc -> map.get(loc.getPath());
	}
	
	public Map<String, ConfigSpecNode> getSpecMap()
	{
		if (this.specMap == null)
		{
			this.specMap = this.spec.valueMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> childNode(entry.getKey(), entry.getValue())));
		}
		return this.specMap;
	}
	
	private ConfigSpecNode childNode(String key, Object spec)
	{
		ConfigLocation location = new ConfigLocation(this.getLoc(), key);
		if (spec instanceof UnmodifiableConfig)
        {
        	return new ConfigTableSpec(location, (UnmodifiableConfig)spec, this.levelComments);
        }
        else
        {
            ValueSpec valueSpec = (ValueSpec)spec;
            return new ConfigPropertySpec<>(location, valueSpec);
        }
	}
	
	public <R> Stream<R> exploreEntries(BiFunction<String, ConfigSpecNode, R> visitor)
	{
		return this.getSpecMap().entrySet().stream().map(entry -> visitor.apply(entry.getKey(), entry.getValue()));
	}
	
	@Override
	public <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
	
}

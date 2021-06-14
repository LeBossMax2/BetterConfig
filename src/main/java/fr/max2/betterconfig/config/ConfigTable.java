package fr.max2.betterconfig.config;

import java.util.Map;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

/**
 * A configuration table and its specification
 */
public class ConfigTable extends ConfigNode
{
	/** The table containing the specification of each entry */
	private final UnmodifiableConfig spec;
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;
	/** The comment associated with the table */
	private final String comment;
	
	public ConfigTable(UnmodifiableConfig spec, UnmodifiableConfig configValues, String comment)
	{
		this.spec = spec;
		this.configValues = configValues;
		this.comment = comment;
	}
	
	@Override
	String getCommentString()
	{
		return this.comment;
	}

	/**
	 * Explores each entries of the map using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the stream of the results of the visitor on each entry
	 */
	public <R> Stream<R> exploreEntries(IConfigEntryVisitor<Void, R> visitor)
	{
		return this.exploreEntries(visitor, null);
	}

	/**
	 * Explores each entries of the map using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the stream of the results of the visitor on each entry
	 */
	public <P, R> Stream<R> exploreEntries(IConfigEntryVisitor<P, R> visitor, P param)
	{
		Map<String, Object> specMap = this.spec.valueMap();
        Map<String, Object> configMap = this.configValues.valueMap();
        
        return specMap.entrySet().stream().map(specEntry ->
        {
        	String key = specEntry.getKey();
            Object specValue = specEntry.getValue();
            Object configValue = configMap.get(key);

            if (specValue instanceof UnmodifiableConfig)
            {
            	String comment = ""; //TODO find a way to replace 'values.getComment(key);'
            	return visitor.visitSubTable(key, new ConfigTable((UnmodifiableConfig)specValue, (UnmodifiableConfig)configValue, comment), param);
            }
            else
            {
                ValueSpec valueSpec = (ValueSpec)specValue;
                return visitor.visitValue(key, new ConfigProperty<>(valueSpec, (ConfigValue<?>)configValue), param);
            }
        });
	}
}

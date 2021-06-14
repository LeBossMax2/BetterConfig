package fr.max2.betterconfig.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * A configuration table and its specification
 */
public class ConfigTable extends ConfigNode
{
	/** The map containing the comment for each node */
	private final Map<List<String>, String> levelComments;
	/** The table containing the specification of each entry */
	private final UnmodifiableConfig spec;
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;
	/** The comment associated with the table */
	private final String comment;
	/** The function to call then the value of a property is changed */
	private Consumer<ConfigProperty<?>> changeListener;
	
	private ConfigTable(Map<List<String>, String> levelComments, UnmodifiableConfig spec, UnmodifiableConfig configValues, Iterable<String> path, Consumer<ConfigProperty<?>> changeListener)
	{
		super(path);
		this.levelComments = levelComments;
		this.spec = spec;
		this.configValues = configValues;
		this.comment = levelComments.get(this.getPath());
		this.changeListener = changeListener;
	}
	
	public ConfigTable(ForgeConfigSpec spec, Consumer<ConfigProperty<?>> changeListener)
	{
		this(getSpecComments(spec), spec.getSpec(), spec.getValues(), Collections.emptyList(), changeListener);
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
            	Iterable<String> subPath = Iterables.concat(this.getPath(), ImmutableList.of(key));
            	return visitor.visitSubTable(key, new ConfigTable(this.levelComments, (UnmodifiableConfig)specValue, (UnmodifiableConfig)configValue, subPath, this.changeListener), param);
            }
            else
            {
                ValueSpec valueSpec = (ValueSpec)specValue;
                return visitor.visitValue(key, new ConfigProperty<>(valueSpec, (ConfigValue<?>)configValue, this.changeListener), param);
            }
        });
	}
	
	/** Gets the comments from the spec */
	private static Map<List<String>, String> getSpecComments(ForgeConfigSpec spec)
	{
		return ObfuscationReflectionHelper.getPrivateValue(ForgeConfigSpec.class, spec, "levelComments");
	}
}

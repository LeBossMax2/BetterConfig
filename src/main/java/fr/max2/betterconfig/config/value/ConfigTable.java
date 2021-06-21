package fr.max2.betterconfig.config.value;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;

public abstract class ConfigTable extends ConfigNode<ConfigTableSpec>
{
	/** The function to call then the value of a property is changed */
	protected Consumer<ConfigProperty<?>> changeListener;

	protected ConfigTable(ConfigTableSpec spec, Consumer<ConfigProperty<?>> changeListener)
	{
		super(spec);
		this.changeListener = changeListener;
	}
	
	public abstract Map<String, ConfigTableEntry> getValueMap();
	
	public <R> Stream<R> exploreEntries(BiFunction<ConfigTableEntrySpec, ConfigNode<?>, R> visitor)
	{
		return this.getValueMap().entrySet().stream().map(entry -> visitor.apply(entry.getValue().getSpec(), entry.getValue().getNode()));
	}
	
	@Override
	public <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
}

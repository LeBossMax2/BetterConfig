package fr.max2.betterconfig.config.value;

import java.util.Objects;

import fr.max2.betterconfig.config.spec.ConfigValueSpec;

public class ConfigValue<T> extends ConfigNode<ConfigValueSpec<T>>
{
	/** The current temporary value of the property */
	protected T currentValue;
	
	public ConfigValue(ConfigValueSpec<T> spec, T initialValue)
	{
		super(spec);
		this.currentValue = initialValue;
	}
	
	/**
	 * Gets the current configuration value
	 * @return the current temporary value
	 */
	public T getValue()
	{
		return this.getSpec().deepCopy(this.currentValue);
	}
	
	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	public void setValue(T value)
	{
		if (!Objects.equals(this.getValue(), value))
		{
			this.currentValue = this.getSpec().deepCopy(this.currentValue);
			this.onValueChanged();
		}
	}
	
	protected void onValueChanged()
	{ }
	
	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <R> R exploreType(IConfigPropertyVisitor<Void, R> visitor)
	{
		return this.exploreType(visitor, null);
	}

	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <P, R> R exploreType(IConfigPropertyVisitor<P, R> visitor, P param)
	{
		return this.getSpec().getType().exploreProperty(visitor, this, param);
	}
	
	@Override
	public <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitProperty(this, param);
	}
}

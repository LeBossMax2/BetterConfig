package fr.max2.betterconfig.config.value;

import java.util.HashSet;
import java.util.Set;

import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.util.property.IListener;
import fr.max2.betterconfig.util.property.IReadableProperty;

public final class ConfigPrimitive<T> implements IConfigNode, IReadableProperty<T>
{
	private final Set<IListener<? super T>> listeners = new HashSet<>();
	private final IConfigPrimitiveSpec<T> spec;
	private T initialValue;
	private T currentValue;
	
	public ConfigPrimitive(IConfigPrimitiveSpec<T> spec)
	{
		this.spec = spec;
		this.initialValue = spec.getDefaultValue();
		this.currentValue = this.initialValue;
	}
	
	@Override
	public IConfigPrimitiveSpec<T> getSpec()
	{
		return this.spec;
	}

	/**
	 * Gets the current configuration value
	 * @return the current temporary value
	 */
	@Override
	public T getValue()
	{
		return this.currentValue;
	}

	/**
	 * Sets the configuration value
	 * @param value the new value
	 */
	public void setValue(T newValue)
	{
		this.currentValue = newValue;
		this.onValiChanged();
	}
	
	@Override
	public void setAsInitialValue()
	{
		this.initialValue = this.currentValue;
	}
	
	@Override
	public void undoChanges()
	{
		this.setValue(this.initialValue);
	}

	@Override
	public void onChanged(IListener<? super T> listener)
	{
		this.listeners.add(listener);
	}
	
	@Override
	public void removeOnChangedListener(IListener<? super T> listener)
	{
		this.listeners.remove(listener);
	}
	
	private void onValiChanged()
	{
		this.listeners.forEach(l -> l.onValueChanged(this.currentValue));
	}
	
	@Override
	public String toString()
	{
		return this.getValue().toString();
	}
	
	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <R> R exploreType(IConfigPrimitiveVisitor<Void, R> visitor)
	{
		return this.exploreType(visitor, null);
	}

	/**
	 * Explores this property using the given visitor
	 * @param <R> the result type of the visitor
	 * @param visitor
	 * @return the result of the visitor
	 */
	public <P, R> R exploreType(IConfigPrimitiveVisitor<P, R> visitor, P param)
	{
		return this.getSpec().getType().exploreProperty(visitor, this, param);
	}
}

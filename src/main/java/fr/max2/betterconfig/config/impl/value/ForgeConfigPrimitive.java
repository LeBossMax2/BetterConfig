package fr.max2.betterconfig.config.impl.value;

import java.util.HashSet;
import java.util.Set;

import fr.max2.betterconfig.config.impl.IForgeNodeInfo;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.util.property.IListener;

public class ForgeConfigPrimitive<T, Info extends IForgeNodeInfo> extends ForgeConfigNode<T, IConfigPrimitiveSpec<T>, Info> implements IConfigPrimitive<T>
{
	protected final Set<IListener<? super T>> listeners = new HashSet<>();
	private final T initialValue;
	private T currentValue;
	
	public ForgeConfigPrimitive(IConfigPrimitiveSpec<T> spec, Info info, T initialValue)
	{
		super(spec, info);
		this.initialValue = initialValue;
		this.currentValue = initialValue;
	}
	
	@Override
	protected T getCurrentValue()
	{
		return this.getValue();
	}

	@Override
	public T getValue()
	{
		return this.currentValue;
	}

	@Override
	public void setValue(T newValue)
	{
		this.currentValue = newValue;
		this.onValiChanged();
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
	
	protected void onValiChanged()
	{
		this.listeners.forEach(l -> l.onValueChanged(this.currentValue));
	}
	
	@Override
	public String toString()
	{
		return this.getValue().toString();
	}
}

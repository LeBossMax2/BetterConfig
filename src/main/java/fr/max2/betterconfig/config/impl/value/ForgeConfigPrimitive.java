package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.impl.IForgeNodeInfo;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.value.IConfigPrimitive;

public class ForgeConfigPrimitive<T, Info extends IForgeNodeInfo> extends ForgeConfigNode<T, IConfigPrimitiveSpec<T>, Info> implements IConfigPrimitive<T>
{
	private final List<Runnable> elemChangeListeners = new ArrayList<>();
	private T currentValue;
	
	public ForgeConfigPrimitive(IConfigPrimitiveSpec<T> spec, Info info, T initialValue)
	{
		super(spec, info);
		this.currentValue = initialValue;
	}
	
	public ForgeConfigPrimitive<T, Info> addChangeListener(Runnable listener)
	{
		this.elemChangeListeners.add(listener);
		return this;
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
	public void setValue(T value)
	{
		this.currentValue = value;
		this.elemChangeListeners.forEach(Runnable::run);
	}
}

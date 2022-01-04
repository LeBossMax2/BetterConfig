package fr.max2.betterconfig.client.gui.style;

import javax.annotation.Nullable;

public class StyleSetEffect<T> implements IStyleEffect<T>
{
	public final T value;

	public StyleSetEffect(T value)
	{
		this.value = value;
	}

	@Override
	public T updateValue(@Nullable T prevValue, @Nullable T defaultValue)
	{
		return this.value;
	}
	
	@Override
	public String typeName()
	{
		return "set";
	}
	
	@Override
	public String toString()
	{
		return "= " + this.value;
	}
}

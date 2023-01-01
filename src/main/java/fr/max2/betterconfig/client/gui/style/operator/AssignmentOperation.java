package fr.max2.betterconfig.client.gui.style.operator;

import javax.annotation.Nullable;

public class AssignmentOperation<T> implements IStyleOperation<T>
{
	private final T value;

	public AssignmentOperation(T value)
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

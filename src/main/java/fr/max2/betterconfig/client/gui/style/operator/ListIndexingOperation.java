package fr.max2.betterconfig.client.gui.style.operator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ListIndexingOperation<T> implements IStyleOperation<List<T>>
{
	private final int index;
	private final IStyleOperation<T> elementOperation;
	
	public ListIndexingOperation(int index, IStyleOperation<T> elementOperation)
	{
		this.index = index;
		this.elementOperation = elementOperation;
	}
	
	@Override
	public List<T> updateValue(@Nullable List<T> values, @Nullable List<T> defaultValue)
	{
		if (values == null)
		{
			values = new ArrayList<>();
			if (defaultValue != null)
				values.addAll(defaultValue);
		}
		while (values.size() <= this.index)
			values.add(null);

		values.set(this.index, this.elementOperation.updateValue(values.get(this.index), null));
		
		return values;
	}
	
	@Override
	public String typeName()
	{
		return "item";
	}
}

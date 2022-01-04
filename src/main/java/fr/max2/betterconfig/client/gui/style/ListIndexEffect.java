package fr.max2.betterconfig.client.gui.style;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ListIndexEffect<T> implements IStyleEffect<List<T>>
{
	private final int index;
	private final IStyleEffect<T> elementEffect;
	
	public ListIndexEffect(int index, IStyleEffect<T> elementEffect)
	{
		this.index = index;
		this.elementEffect = elementEffect;
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
		while (values.size() < this.index)
			values.add(null);

		values.set(this.index, this.elementEffect.updateValue(values.get(this.index), null));
		
		return values;
	}
	
	@Override
	public String typeName()
	{
		return "item";
	}
}

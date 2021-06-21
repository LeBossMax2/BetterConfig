package fr.max2.betterconfig.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import fr.max2.betterconfig.config.spec.ConfigListSpec;
import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.ConfigNode;
import fr.max2.betterconfig.config.value.ConfigProperty;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigList<T> extends ConfigList<T>
{
	/** The configuration value currently saved */
	private final ConfigValue<List<T>> configValue;
	
	private List<ElementProperty> valueList;
	private List<ConfigNode<?>> valueListView;

	public ForgeConfigList(ConfigListSpec<T> spec, ConfigValue<List<T>> configValue)
	{
		super(spec, configValue.get());
		this.configValue = configValue;
	}
	
	@Override
	protected List<T> getSavedValue()
	{
		return this.configValue.get();
	}
	
	@Override
	protected void setSavedValue(List<T> value)
	{
		this.configValue.set(value);
	}

	@Override
	public List<ConfigNode<?>> getValueList()
	{
		if (this.valueList == null)
		{
			List<T> values = this.getValue();
			this.valueList = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
			{
				this.valueList.add(new ElementProperty(values.get(i), i));
			}
			this.valueListView = Collections.unmodifiableList(this.valueList);
		}
		return this.valueListView;
	}
	
	@Override
	protected void onValueChanged()
	{
		if (this.valueList != null)
		{
			// Remove extra properties
			while (this.valueList.size() > this.currentValue.size())
			{
				this.valueList.remove(this.valueList.size() - 1);
			}
			
			// Update existing properties
			for (int i = 0; i < this.valueList.size(); i++)
			{
				this.valueList.get(i).setVal(this.currentValue.get(i));
			}
			
			// Create new properties
			while (this.valueList.size() < this.currentValue.size())
			{
				int index = this.valueList.size();
				this.valueList.add(new ElementProperty(this.currentValue.get(index), index));
			}
		}
		super.onValueChanged();
	}

	@Override
	public ConfigNode<?> addValue(T value)
	{
		List<ConfigNode<?>> list = this.getValueList();
		ConfigNode<?> newNode = new ElementProperty(value, list.size());
		this.currentValue.add(value);
		list.add(newNode);
		this.changeListener.accept(this);
		return newNode;
	}
	
	public void setValue(int index, T value)
	{
		T val = this.getSpec().getElementSpec().deepCopy(value);
		this.currentValue.set(index, val);
		if (this.valueList != null)
		{
			this.valueList.get(index).setVal(val);
		}
		this.changeListener.accept(this);
	}
	
	public class ElementProperty extends fr.max2.betterconfig.config.value.ConfigValue<T>
	{
		private int index;
		
		public ElementProperty(T initialValue, int index)
		{
			super(ForgeConfigList.this.getSpec().getElementSpec(), initialValue);
			this.index = index;
		}
		
		private void setVal(T value)
		{
			this.currentValue = value;
		}
		
		@Override
		protected void onValueChanged()
		{
			ForgeConfigList.this.currentValue.set(this.index, this.getValue());
			ForgeConfigList.this.changeListener.accept(ForgeConfigList.this);
		}
	}
}

package fr.max2.betterconfig.util.property;


public interface IListener<T>
{
	void onValueChanged(T newValue);
}

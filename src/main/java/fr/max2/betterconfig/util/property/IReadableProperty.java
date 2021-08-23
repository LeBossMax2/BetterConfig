package fr.max2.betterconfig.util.property;

public interface IReadableProperty<T>
{
	T getValue();
	
	void onChanged(IListener<? super T> listener);
	void removeOnChangedListener(IListener<? super T> listener);
}

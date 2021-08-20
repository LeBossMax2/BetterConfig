package fr.max2.betterconfig.util.property;

public interface IReadableProperty<T>
{
	T get();
	
	void onChanged(IListener<T> listener);
	void removeOnChangedListener(IListener<? super T> listener);
}

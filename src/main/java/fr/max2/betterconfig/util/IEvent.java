package fr.max2.betterconfig.util;

public interface IEvent<Listener>
{
	void add(Listener listener);
	
	void remove(Listener listener);
}

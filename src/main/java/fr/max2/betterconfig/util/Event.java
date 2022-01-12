package fr.max2.betterconfig.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Event<Listener> implements IEvent<Listener>
{
	private List<Listener> listeners = new ArrayList<>();

	@Override
	public void add(Listener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void remove(Listener listener)
	{
		this.listeners.remove(listener);
	}

	public void call(Consumer<Listener> action)
	{
		this.listeners.forEach(action);
	}
}

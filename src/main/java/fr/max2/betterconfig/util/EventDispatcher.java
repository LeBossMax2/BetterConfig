package fr.max2.betterconfig.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class EventDispatcher<Listener> implements IEvent<Listener>
{
	private Collection<Listener> listeners = new ArrayList<>();

	private EventDispatcher(Collection<Listener> listeners)
	{
		this.listeners = listeners;
	}

	public static <L> EventDispatcher<L> ordered()
	{
		return new EventDispatcher<>(new ArrayList<>());
	}

	public static <L> EventDispatcher<L> unordered()
	{
		return new EventDispatcher<>(new HashSet<>());
	}

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

	public void dispatch(Consumer<Listener> action)
	{
		this.listeners.forEach(action);
	}
}

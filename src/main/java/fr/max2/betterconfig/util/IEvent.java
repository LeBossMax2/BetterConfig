package fr.max2.betterconfig.util;

public interface IEvent<Listener>
{
	Guard add(Listener listener);

	public interface Guard extends AutoCloseable
	{
		@Override
		void close();
	}
}

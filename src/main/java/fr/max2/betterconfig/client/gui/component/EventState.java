package fr.max2.betterconfig.client.gui.component;


public class EventState
{
	private boolean consumed = false;
	
	public boolean isConsumed()
	{
		return this.consumed;
	}
	
	public void consume()
	{
		this.consumed = true;
	}
}

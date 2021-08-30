package fr.max2.betterconfig.client.gui.component;


public class CycleFocusState extends EventState
{
	private boolean propagated;
	
	public boolean isPropagated()
	{
		return this.propagated;
	}
	
	public void propagate()
	{
		if (this.isConsumed())
		{
			throw new IllegalStateException("The event is already consumed so can't be propagated");
		}
		this.propagated = true;
	}
	
	@Override
	public void consume()
	{
		this.propagated = false;
		super.consume();
	}
}

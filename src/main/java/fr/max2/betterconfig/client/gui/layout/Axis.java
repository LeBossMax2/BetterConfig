package fr.max2.betterconfig.client.gui.layout;


public enum Axis
{
	HORIZONTAL,
	VERTICAL;
	
	public Axis perpendicular()
	{
		return switch (this)
		{
			case HORIZONTAL -> VERTICAL;
			case VERTICAL -> HORIZONTAL;
		};
	}
}

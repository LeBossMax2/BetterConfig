package fr.max2.betterconfig.client.gui.layout;


public enum Axis
{
	HORIZONTAL,
	VERTICAL;
	
	public Axis perpendicular()
	{
		switch (this)
		{
		case HORIZONTAL:
			return VERTICAL;
		case VERTICAL:
			return HORIZONTAL;
		default:
			return null;
		}
	}
}

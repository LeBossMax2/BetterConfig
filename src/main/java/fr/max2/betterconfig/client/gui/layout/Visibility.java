package fr.max2.betterconfig.client.gui.layout;

public enum Visibility
{
	VISIBLE,
	HIDDEN,
	COLLAPSED;
	
	public boolean isVisible()
	{
		return this == VISIBLE;
	}
	
	public boolean isCollapsed()
	{
		return this == COLLAPSED;
	}
}

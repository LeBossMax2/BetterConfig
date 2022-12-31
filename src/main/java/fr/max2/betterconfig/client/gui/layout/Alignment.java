package fr.max2.betterconfig.client.gui.layout;


public enum Alignment
{
	MIN,
	CENTER,
	MAX;
	// TODO [#2] Implement justified alignment
	
	public int getOffset(int availableSpace)
	{
		return switch (this)
		{
			case MIN	-> 0;
			case CENTER	-> (availableSpace + 1) / 2;
			case MAX	-> availableSpace;
		};
	}
}

package fr.max2.betterconfig.client.gui.layout;


public enum Alignment
{
	MIN,
	CENTER,
	MAX;
	// TODO [#2] Implement justified alignment
	
	public int getOffset(int availableSpace)
	{
		switch (this)
		{
		default:
		case MIN:
			return 0;
		case CENTER:
			return availableSpace / 2;
		case MAX:
			return availableSpace;
		}
	}
}

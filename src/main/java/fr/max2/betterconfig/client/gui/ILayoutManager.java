package fr.max2.betterconfig.client.gui;

/**
 * Manages the layout of a user interface
 */
public interface ILayoutManager
{
	public static final ILayoutManager NONE = new ILayoutManager()
	{
		@Override
		public void marksLayoutDirty()
		{ }
	};
	
	/**
	 * Marks the layout dirty in order to update the layout
	 */
	public void marksLayoutDirty();
	
	/**
	 * Gets the x coordinate of the start of the layout
	 */
	public default int getLayoutX()
	{
		return 0;
	}

	/**
	 * Gets the x coordinate of the start of the layout
	 */
	public default int getLayoutY()
	{
		return 0;
	}
}

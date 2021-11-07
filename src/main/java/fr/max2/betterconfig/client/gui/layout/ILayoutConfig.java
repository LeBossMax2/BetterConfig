package fr.max2.betterconfig.client.gui.layout;

public interface ILayoutConfig<Param>
{
	Size measureLayout(Param param);
	
	Rectangle computeLayout(Rectangle availableRect, Param param);
}

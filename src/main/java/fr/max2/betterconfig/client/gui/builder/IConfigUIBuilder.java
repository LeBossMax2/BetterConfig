package fr.max2.betterconfig.client.gui.builder;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;

public interface IConfigUIBuilder<P>
{
	ITableUIBuilder<P> start(BetterConfigScreen screen);
}

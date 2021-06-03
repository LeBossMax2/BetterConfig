package fr.max2.betterconfig.client.gui.builder;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;

public interface ConfigUIBuilder<P>
{
	TableUIBuilder<P> start(BetterConfigScreen screen);
}

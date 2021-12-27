package fr.max2.betterconfig.client.gui;

import fr.max2.betterconfig.client.gui.better.BetterConfigBuilder;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.config.value.IConfigTable;

@FunctionalInterface
public interface IConfigUIBuilder
{
	static final IConfigUIBuilder DEFAULT = BetterConfigBuilder::build;
	
	IComponent build(BetterConfigScreen screen, IConfigTable config);
}
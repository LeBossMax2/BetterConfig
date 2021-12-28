package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.style.StyleRule;

public class HBox extends CompositeComponent
{
	public static final StyleRule STYLE = StyleRule.when().type("hbox").then().set(CompositeLayoutConfig.DIR, Axis.HORIZONTAL).build();

	public HBox(List<IComponent> children)
	{
		super("hbox", children);
	}
	
}

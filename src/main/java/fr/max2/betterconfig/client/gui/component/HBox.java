package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.style.StyleRule;

public class HBox extends CompositeComponent
{
	public static final StyleRule STYLE = StyleRule.when().equals(COMPONENT_TYPE, "hbox").then().set(CompositeLayoutConfig.DIR, Axis.HORIZONTAL).build();
	
	private final List<IComponent> children; 

	public HBox(IComponentParent layoutManager, List<IComponent> children)
	{
		super(layoutManager, "hbox");
		this.children = children;
	}

	@Override
	public List<? extends IComponent> getChildren()
	{
		return this.children;
	}
	
}

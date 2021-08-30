package fr.max2.betterconfig.client.gui.component;

import java.util.List;

import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;

public class HBox extends CompositeComponent
{
	private final List<IComponent> children; 
	public final CompositeLayoutConfig config = new CompositeLayoutConfig();

	public HBox(IComponentParent layoutManager, List<IComponent> children)
	{
		super(layoutManager);
		this.children = children;
		this.config.dir = Axis.HORIZONTAL;
	}

	@Override
	public List<? extends IComponent> getChildren()
	{
		return this.children;
	}

	@Override
	protected CompositeLayoutConfig getLayoutConfig()
	{
		return this.config;
	}
	
}

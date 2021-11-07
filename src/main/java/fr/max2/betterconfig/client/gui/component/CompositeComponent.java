package fr.max2.betterconfig.client.gui.component;

import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;

public abstract class CompositeComponent extends Component<ICompositeComponent> implements ICompositeComponent
{
	public CompositeComponent(IComponentParent layoutManager, String type)
	{
		super(layoutManager, type);
	}
	
	@Override
	protected CompositeLayoutConfig getLayoutConfig()
	{
		return CompositeLayoutConfig.INSTANCE;
	}

	@Override
	protected ICompositeComponent getLayoutParam()
	{
		return this;
	}
}

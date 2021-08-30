package fr.max2.betterconfig.client.gui.component;

import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;

public abstract class CompositeComponent extends Component<ICompositeComponent> implements ICompositeComponent
{
	public CompositeComponent(IComponentParent layoutManager)
	{
		super(layoutManager);
	}
	
	@Override
	protected abstract CompositeLayoutConfig getLayoutConfig();

	@Override
	protected ICompositeComponent getLayoutParam()
	{
		return this;
	}
}

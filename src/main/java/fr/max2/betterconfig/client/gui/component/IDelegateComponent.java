package fr.max2.betterconfig.client.gui.component;

import java.util.List;

public interface IDelegateComponent extends ICompositeComponent
{
	IComponent getChild();
	
	@Override
	default List<? extends IComponent> getChildren()
	{
		return List.of(this.getChild());
	}
}

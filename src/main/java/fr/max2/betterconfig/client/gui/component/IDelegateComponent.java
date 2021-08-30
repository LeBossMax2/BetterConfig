package fr.max2.betterconfig.client.gui.component;

import java.util.Arrays;
import java.util.List;

public interface IDelegateComponent extends ICompositeComponent
{
	IComponent getChild();
	
	@Override
	default List<? extends IComponent> getChildren()
	{
		return Arrays.asList(this.getChild());
	}
}

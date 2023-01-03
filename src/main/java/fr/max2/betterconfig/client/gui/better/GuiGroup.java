package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.IComponent;

/** The ui for a group of components */
public class GuiGroup extends CompositeComponent implements IBetterElement
{
	public GuiGroup(List<IComponent> content)
	{
		super("better:group", content);
	}

	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		boolean allHidden = true;
		for (IComponent child : this.children)
		{
			if (child instanceof IBetterElement betterChild)
				allHidden &= betterChild.filterElements(filter);
		}
		if (filter.matches())
			return false;
		return allHidden;
	}
}
package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.config.ConfigFilter;

/** The ui for a group of components */
public class GuiGroup extends CompositeComponent implements IBetterElement
{
	/** The list of entries of the group */
	private final List<IBetterElement> betterElements;

	public GuiGroup(List<? extends IComponent> content)
	{
		super("better:group", content);
		// TODO [#2] Create custom filter readable list
		this.betterElements = this.children.stream().filter(cmp -> cmp instanceof IBetterElement).map(cmp -> (IBetterElement)cmp).toList();
	}
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		boolean allHidden = true;
		for (IBetterElement child : this.betterElements)
		{
			allHidden &= child.filterElements(filter);
		}
		if (filter == ConfigFilter.ALL)
			return false;
		return allHidden;
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
	}
}
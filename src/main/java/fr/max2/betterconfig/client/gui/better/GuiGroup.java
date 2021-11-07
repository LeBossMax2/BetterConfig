package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.config.ConfigFilter;

/** The ui for a group of components */
public class GuiGroup extends CompositeComponent implements IBetterElement
{
	/** The list of entries of the group */
	private final List<IBetterElement> betterElements;
	private final List<? extends IComponent> content;

	public GuiGroup(IComponentParent layoutManager, List<? extends IComponent> content)
	{
		super(layoutManager, "better:group");
		this.content = content;
		this.betterElements = content.stream().filter(cmp -> cmp instanceof IBetterElement).map(cmp -> (IBetterElement)cmp).toList();
	}

	public void updateLayout()
	{
		this.layoutManager.marksLayoutDirty();
	}
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		boolean anyVisible = false;
		for (IBetterElement child : this.betterElements)
		{
			anyVisible |= child.filterElements(filter);
		}
		return anyVisible;
	}

	@Override
	public List<? extends IComponent> getChildren()
	{
		return this.content;
	}
	
	@Override
	public void invalidate()
	{
		// No sure why we need this but can't compile without
		super.invalidate();
	}
}
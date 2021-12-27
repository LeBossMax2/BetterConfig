package fr.max2.betterconfig.client.gui.better;

import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.widget.ScrollPane;
import fr.max2.betterconfig.config.ConfigFilter;

public class BetterScrollPane extends ScrollPane implements IBetterElement
{
	public BetterScrollPane(IComponent content)
	{
		super(content);
		this.addClass("better:scroll_pane");
	}
	
	// Layout
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		((IBetterElement)this.getChild()).filterElements(filter);
		return false;
	}
}

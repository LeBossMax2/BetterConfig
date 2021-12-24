package fr.max2.betterconfig.client.gui.better;

import java.util.function.Function;

import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.widget.ScrollPane;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.Minecraft;

public class BetterScrollPane extends ScrollPane implements IBetterElement
{
	public BetterScrollPane(IComponentParent layoutManager, Minecraft minecraft, Function<IComponentParent, IComponent> content)
	{
		super(layoutManager, minecraft, content);
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

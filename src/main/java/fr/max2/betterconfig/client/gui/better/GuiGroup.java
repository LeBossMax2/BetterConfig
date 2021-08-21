package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.gui.FocusableGui;

/** The ui for a group of components */
public class GuiGroup extends FocusableGui implements INestedGuiComponent, IBetterElement
{
	/** The list of entries of the group */
	private final List<IBetterElement> content;
	private final int width;
	private int height = 0;
	private ILayoutManager layout;

	public GuiGroup(int width, List<IBetterElement> content)
	{
		this.content = content;
		this.width = width;
	}

	public void updateLayout()
	{
		if (this.layout != null)
		{
			INestedGuiComponent.super.setLayoutManager(this.layout);
			this.layout.marksLayoutDirty();
		}
	}

	@Override
	public List<? extends IGuiComponent> getEventListeners()
	{
		return this.content;
	}
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		INestedGuiComponent.super.setLayoutManager(manager);
	}
	
	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		int h = 0;
		for (IBetterElement elem : this.content)
		{
			h += elem.setYgetHeight(y + h, filter);
		}
		this.height = h;
		return h;
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}
}
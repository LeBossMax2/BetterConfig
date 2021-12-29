package fr.max2.betterconfig.client.gui.layout;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.style.StyleProperty;
import net.minecraft.resources.ResourceLocation;

public final class ComponentLayoutConfig
{
	private ComponentLayoutConfig()
	{ }
	
	public static final StyleProperty<Size> SIZE_OVERRIDE = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "size_override"), new Size());
	public static final StyleProperty<Padding> OUTER_PADDING = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "outer_padding"), new Padding());
	public static final StyleProperty<Visibility> VISIBILITY = new StyleProperty<>(new ResourceLocation(BetterConfig.MODID, "visibility"), Visibility.VISIBLE);
	
	public static Size getParentRequiredSize(IComponent component, Size childSize)
	{
		return component.getStyleProperty(OUTER_PADDING).unpad(childSize);
	}
	
	public static Rectangle getChildRect(IComponent component, Rectangle parentAvailableRect)
	{
		return component.getStyleProperty(OUTER_PADDING).pad(parentAvailableRect);
	}
}

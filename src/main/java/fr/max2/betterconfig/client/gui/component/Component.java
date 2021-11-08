package fr.max2.betterconfig.client.gui.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.layout.ILayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.IStylableComponent;
import fr.max2.betterconfig.client.gui.style.ListPropertyIdentifier;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import fr.max2.betterconfig.client.gui.style.StyleProperty;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class Component<LP> extends GuiComponent implements IStylableComponent
{
	public static PropertyIdentifier<String> COMPONENT_TYPE = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "component_type"), String.class);
	public static ListPropertyIdentifier<String> COMPONENT_CLASSES = new ListPropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "component_classes"), String.class);
	
	protected final Map<PropertyIdentifier<?>, Supplier<?>> propertyMap = new HashMap<>();
	protected final Map<StyleProperty<?>, Object> styleOverride = new HashMap<>();
	protected final String type;
	protected final List<String> classes = new ArrayList<>();
	protected final IComponentParent layoutManager;
	protected Size prefSize;
	protected Rectangle relativeRect;
	protected Rectangle absoluteRect = new Rectangle();
	
	public Component(IComponentParent layoutManager, String type)
	{
		this.layoutManager = layoutManager;
		this.type = type;
		
		this.registerProperty(COMPONENT_TYPE, () -> this.type);
		this.registerProperty(COMPONENT_CLASSES, () -> this.classes);
	}
	
	// Style
	
	protected <T> void registerProperty(PropertyIdentifier<T> property, Supplier<T> valueSupplier)
	{
		this.propertyMap.put(property, valueSupplier);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(PropertyIdentifier<T> property) {
		return (T)this.propertyMap.get(property).get();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getStyleProperty(StyleProperty<T> property)
	{
		Object value = this.styleOverride.get(property);
		if (value == null)
			return this.layoutManager.getStyleSheet().computePropertyValue(this, property);
		return (T)value;
	}
	
	public <T> void setStyle(StyleProperty<T> property, T value)
	{
		this.styleOverride.put(property, value);
	}
	
	public Component<LP> addClass(String clazz)
	{
		this.classes.add(clazz);
		return this;
	}

	// Layout
	
	protected abstract LP getLayoutParam();

	protected abstract ILayoutConfig<? super LP> getLayoutConfig();
	
	@Override
	public Size measureLayout()
	{
		Size prefSize = this.getLayoutConfig().measureLayout(this.getLayoutParam());
		this.setPrefSize(prefSize);
		return prefSize;
	}
	
	@Override
	public void computeLayout(Rectangle availableRect)
	{
		this.setRelativeRect(this.getLayoutConfig().computeLayout(availableRect, this.getLayoutParam()));
	}

	@Override
	public Size getPrefSize()
	{
		return this.prefSize;
	}
	
	protected Size getSize()
	{
		return this.relativeRect.size;
	}
	
	protected Rectangle getRect()
	{
		this.absoluteRect.x = this.layoutManager.getLayoutX() + this.relativeRect.x;
		this.absoluteRect.y = this.layoutManager.getLayoutY() + this.relativeRect.y;
		return this.absoluteRect;
	}

	protected void setPrefSize(Size prefSize)
	{
		this.prefSize = prefSize;
	}

	protected void setRelativeRect(Rectangle rect)
	{
		this.absoluteRect.size.width = rect.size.width;
		this.absoluteRect.size.height = rect.size.height;
		this.relativeRect = rect;
	}
	
	protected boolean isPointInside(double x, double y)
	{
		return this.getRect().isPointInside(x, y);
	}
}

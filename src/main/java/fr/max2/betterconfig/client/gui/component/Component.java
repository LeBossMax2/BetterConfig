package fr.max2.betterconfig.client.gui.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.ILayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.ListPropertyIdentifier;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import fr.max2.betterconfig.client.gui.style.StyleProperty;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnTooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;
import net.minecraft.resources.ResourceLocation;

public abstract class Component<LP> extends GuiComponent implements IComponent
{
	public static final PropertyIdentifier<String> COMPONENT_TYPE = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "component_type"), String.class);
	public static final ListPropertyIdentifier<String> COMPONENT_CLASSES = new ListPropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "component_classes"), String.class);
	public static final PropertyIdentifier<IComponent> PARENT = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "parent"), IComponent.class);
	public static final PropertyIdentifier<Boolean> HOVERED = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "hovered"), Boolean.class);
	public static final PropertyIdentifier<Boolean> FOCUSED = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "focused"), Boolean.class);

	public static final OnTooltip NO_OVERLAY = Button.NO_TOOLTIP;
	
	protected final Map<PropertyIdentifier<?>, Supplier<?>> propertyMap = new HashMap<>();
	protected final Map<StyleProperty<?>, Object> styleOverride = new HashMap<>();
	protected final String type;
	protected final List<String> classes = new ArrayList<>();
	protected IComponentParent layoutManager;
	protected IComponent parent;
	protected Size prefSize;
	protected Rectangle relativeRect;
	private Rectangle absoluteRect = new Rectangle();
	protected boolean hovered = false;
	protected boolean hasFocus = false;
	/** The overlay to show when the mouse is over the component */
	protected OnTooltip overlay = NO_OVERLAY;
	
	public Component(String type)
	{
		this.type = type;
		
		this.registerProperty(COMPONENT_TYPE, () -> this.type);
		this.registerProperty(COMPONENT_CLASSES, () -> this.classes);
		this.registerProperty(PARENT, () -> this.parent);
		this.registerProperty(HOVERED, () -> this.isHovered());
		this.registerProperty(FOCUSED, () -> this.hasFocus());
	}
	
	@Override
	public void init(IComponentParent layoutManager, IComponent parent)
	{
		this.layoutManager = layoutManager;
		this.parent = parent;
	}
	
	// Style
	
	protected <T> void registerProperty(PropertyIdentifier<T> property, Supplier<T> valueSupplier)
	{
		this.propertyMap.put(property, valueSupplier);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(PropertyIdentifier<T> property) {
		Supplier<?> propertyValue = this.propertyMap.get(property);
		if (propertyValue == null)
			return null;
		return (T)propertyValue.get();
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
	
	// Rendering

	@Override
	public final void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick)
	{
		this.updateHoverState(pMouseX, pMouseY);
		
		if (!this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isVisible())
			return;
		
		this.onRender(pPoseStack, pMouseX, pMouseY, pPartialTick);
	}
	
	protected abstract void onRender(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick);

	@Override
	public final void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state)
	{
		if (!this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isVisible())
			return;
		
		this.onRenderOverlay(matrixStack, mouseX, mouseY, partialTicks, state);
	}

	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state)
	{
		if (this.isHovered() && !state.isConsumed() && this.overlay != NO_OVERLAY)
		{
			state.consume();
			this.overlay.onTooltip(null, matrixStack, mouseX, mouseY);
		}
	}
	
	// Mouse Handling
	
	protected boolean isPointInside(double x, double y)
	{
		return this.getRect().isPointInside(x, y);
	}

	@Override
	public final void mouseMoved(double mouseX, double mouseY)
	{
		this.updateHoverState(mouseX, mouseY);
		
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onMouseMoved(mouseX, mouseY);
	}
	
	protected abstract void onMouseMoved(double mouseX, double mouseY);

	@Override
	public final void mouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		this.updateHoverState(mouseX, mouseY);
		
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onMouseClicked(mouseX, mouseY, button, state);
	}

	protected abstract void onMouseClicked(double mouseX, double mouseY, int button, EventState state);

	@Override
	public final void mouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		this.updateHoverState(mouseX, mouseY);
		
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onMouseReleased(mouseX, mouseY, button, state);
	}

	protected abstract void onMouseReleased(double mouseX, double mouseY, int button, EventState state);

	@Override
	public final void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		this.updateHoverState(mouseX, mouseY);
		
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onMouseDragged(mouseX, mouseY, button, dragX, dragY, state);
	}

	protected abstract void onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state);

	@Override
	public final void mouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		this.updateHoverState(mouseX, mouseY);
		
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onMouseScrolled(mouseX, mouseY, delta, state);
	}

	protected abstract void onMouseScrolled(double mouseX, double mouseY, double delta, EventState state);
	
	public boolean isHovered()
	{
		return this.hovered;
	}

	private void updateHoverState(double mouseX, double mouseY)
	{
		this.hovered = this.isPointInside(mouseX, mouseY);
	}
	
	// Input handling

	@Override
	public final void keyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onKeyPressed(keyCode, scanCode, modifiers, state);
	}

	protected abstract void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state);

	@Override
	public final void keyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onKeyReleased(keyCode, scanCode, modifiers, state);
	}

	protected abstract void onKeyReleased(int keyCode, int scanCode, int modifiers, EventState state);

	@Override
	public final void charTyped(char codePoint, int modifiers, EventState state)
	{
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onCharTyped(codePoint, modifiers, state);
	}
	
	protected abstract void onCharTyped(char codePoint, int modifiers, EventState state);

	@Override
	public final void cycleFocus(boolean forward, CycleFocusState state)
	{
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return;
		
		this.onCycleFocus(forward, state);
	}
	
	protected abstract void onCycleFocus(boolean forward, CycleFocusState state);
	
	@Override
	public boolean hasFocus()
	{
		return this.hasFocus;
	}
	
	// Narration
	
	@Override
	public NarrationPriority narrationPriority()
	{
		if (this.hasFocus())
			return NarrationPriority.FOCUSED;
		
		if (this.isHovered())
			return NarrationPriority.HOVERED;
		
		return NarrationPriority.NONE;
	}
	
	@Override
	public boolean isActive()
	{
		if (this.getStyleProperty(ComponentLayoutConfig.VISIBILITY).isCollapsed())
			return false;
		
		return IComponent.super.isActive();
	}
	
	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		if (this.overlay == NO_OVERLAY)
			return;
		
		List<net.minecraft.network.chat.Component> hints = new ArrayList<>();
		this.overlay.narrateTooltip(hints::add);
		if (!hints.isEmpty())
			narrationOutput.add(NarratedElementType.HINT, NarrationThunk.from(hints));
	}
	
}

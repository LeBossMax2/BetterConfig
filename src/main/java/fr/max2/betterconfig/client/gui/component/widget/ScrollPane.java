package fr.max2.betterconfig.client.gui.component.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;

import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.component.CycleFocusState;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.component.IComponentParent;
import fr.max2.betterconfig.client.gui.component.IScrollComponent;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.ScrollPaneLayout;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public class ScrollPane extends Component<IScrollComponent> implements IScrollComponent, IComponentParent
{
	protected final IComponent content;

	protected boolean scrolling = false;
	private float scrollDistance = 0.0F;
	protected int scrollBarWidth = 6;
	protected Size contentSize;
	
	/** Indicates whether the layout is dirty */
	protected boolean layoutDirty = true;;
	
	public ScrollPane(IComponent content)
	{
		super("scroll_pane");
		this.content = content;
		this.content.init(this, this);
	}
	
	@Override
	public Minecraft getMinecraft()
	{
		return this.layoutManager.getMinecraft();
	}
	
	// Style
	
	@Override
	public StyleSheet getStyleSheet()
	{
		return this.layoutManager.getStyleSheet();
	}
	
	// Layout
	
	@Override
	protected ScrollPaneLayout getLayoutConfig()
	{
		return ScrollPaneLayout.INSTANCE;
	}

	@Override
	protected IScrollComponent getLayoutParam()
	{
		return this;
	}
	
	@Override
	public void setContentSize(Size size)
	{
		this.contentSize = size;
	}
	
	@Override
	public IComponent getChild()
	{
		return this.content;
	}
	
	public int getContentHeight()
	{
		return this.contentSize.height;
	}
	
	@Override
	public void marksLayoutDirty()
	{
		this.layoutDirty = true;
	}
	
	@Override
	public int getLayoutX()
	{
		return this.getRect().x;
	}
	
	@Override
	public int getLayoutY()
	{
		return this.getRect().y - this.getScrollDistance();
	}
	
	@Override
	public void enqueueWork(Runnable action)
	{
		this.layoutManager.enqueueWork(action);
	}
	
	@Override
	protected void setRelativeRect(Rectangle rect)
	{
		super.setRelativeRect(rect);
		this.applyScrollLimits();
	}
	
	@Override
	public Size updateLayout(IComponent component, Size availableSize)
	{
		Size size = IComponentParent.super.updateLayout(component, availableSize);
		this.applyScrollLimits();
		return size;
	}
	
	public void checkLayout()
	{
		if (this.layoutDirty)
		{
			this.layoutDirty = false;
			this.setContentSize(this.updateLayout(this.content, this.getSize()));
		}
	}
	
	// Scroll control functions
	
	protected void applyScrollLimits()
	{
		this.setScrollDistance(this.scrollDistance);
	}

	protected void scroll(float scroll)
	{
		this.setScrollDistance(this.scrollDistance + scroll);
	}
	
	protected void setScrollDistance(float scroll)
	{
		this.scrollDistance = Mth.clamp(scroll, 0.0f, this.getMaxScroll(this.getRect()));
	}
	
	protected int getScrollDistance()
	{
		return (int)this.scrollDistance;
	}
	
	protected int getMaxScroll(Rectangle rect)
	{
		return Math.max(0, this.getContentHeight() - rect.size.height);
	}
	
	protected float getWheelScrollFactor()
	{
		return 10.0f;
	}
	
	protected float getMouseScrollFactor(Rectangle rect)
	{
		int scrollHeight = Math.max(1, this.getMaxScroll(rect));
		
		return -scrollHeight / (float) (rect.size.height - this.getScrollThumbSize(rect));
	}

	private int getScrollThumbSize(Rectangle rect)
	{
		int size = (int) ((float) (rect.size.height * rect.size.height) / this.getContentHeight());
		size = Mth.clamp(size, 32, rect.size.height);
		return size;
	}
	
	protected void ensureAreaVisible(Rectangle area)
	{
		Size viewport = this.relativeRect.size;
		
		float minScroll = area.getBottom() - viewport.height + 1;
		float maxScroll = area.getTop() - 1;
		
		if (this.scrollDistance > maxScroll)
			this.setScrollDistance(maxScroll);
		else if (this.scrollDistance < minScroll)
			this.setScrollDistance(minScroll);
	}
	
	@Override
	public void setAreaOfInterest(Rectangle area)
	{
		this.ensureAreaVisible(area);
		this.layoutManager.setAreaOfInterest(this.relativeRect);
	}
	
	// Mouse handling
	
	@Override
	protected void onMouseMoved(double mouseX, double mouseY)
	{
		this.checkLayout();
		IScrollComponent.super.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	protected void onMouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		this.checkLayout();
		if (!this.isPointInside(mouseX, mouseY))
			return;
		
		IScrollComponent.super.mouseScrolled(mouseX, mouseY, delta, state);
		if (state.isConsumed())
			return;
		
		this.scroll(-(float)delta * this.getWheelScrollFactor());
		state.consume();
	}
	
	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		this.checkLayout();
		if (!this.isPointInside(mouseX, mouseY))
		{
			this.scrolling = false;
			return;
		}

		Rectangle rect = this.getRect();
		
		int scrollBarLeft = rect.getRight() - this.scrollBarWidth;
		
		if (button == 0 && mouseX >= scrollBarLeft)
		{
			// If mouse on scroll bar
			this.scrolling = true;
			state.consume();
		}
		IScrollComponent.super.mouseClicked(mouseX, mouseY, button, state);
	}
	
	@Override
	protected void onMouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		this.checkLayout();
		if (button == 0 && this.scrolling)
		{
			this.scrolling = false;
			state.consume();
		}
		
		IScrollComponent.super.mouseReleased(mouseX, mouseY, button, state);
	}
	
	@Override
	protected void onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		this.checkLayout();
		if (button == 0 && this.scrolling)
		{
			Rectangle rect = this.getRect();
			
			if (mouseY < rect.getTop())
			{
				this.setScrollDistance(0.0f);
			}
			else if (mouseY > rect.getBottom())
			{
				this.setScrollDistance(this.getMaxScroll(rect));
			}
			else
			{
				this.scroll(-(float)dragY * this.getMouseScrollFactor(rect));
			}
			state.consume();
		}

		IScrollComponent.super.mouseDragged(mouseX, mouseY, button, dragX, dragY, state);
	}
	
	// Other input
	
	@Override
	protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		this.checkLayout();
		IScrollComponent.super.keyPressed(keyCode, scanCode, modifiers, state);
	}
	
	@Override
	protected void onKeyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		this.checkLayout();
		IScrollComponent.super.keyReleased(keyCode, scanCode, modifiers, state);
	}
	
	@Override
	protected void onCharTyped(char codePoint, int modifiers, EventState state)
	{
		this.checkLayout();
		IScrollComponent.super.charTyped(codePoint, modifiers, state);
	}
	
	@Override
	protected void onCycleFocus(boolean forward, CycleFocusState state)
	{
		this.checkLayout();
		IScrollComponent.super.cycleFocus(forward, state);
	}
	
	@Override
	public boolean hasFocus()
	{
		return this.content.hasFocus();
	}
	
	// Rendering
	
	protected void drawBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Rectangle rect)
	{
		Matrix4f mat = matrixStack.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		
		int left = rect.getLeft();
		int top = rect.getTop();
		int right = rect.getRight() - this.scrollBarWidth;
		int bot = rect.getBottom();
		
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.vertex(mat, left , bot, 0.0f).uv(left  / 32.0f, (bot + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(mat, right, bot, 0.0f).uv(right / 32.0f, (bot + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(mat, right, top, 0.0f).uv(right / 32.0f, (top + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(mat, left , top, 0.0f).uv(left  / 32.0f, (top + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		tessellator.end();
	}
	
	protected void drawForeground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Rectangle rect)
	{
		int left = rect.getLeft();
		int top = rect.getTop();
		int right = rect.getRight() - this.scrollBarWidth;
		int bot = rect.getBottom();
		
		int shadingHeight = 4;
		
		this.fillGradient(matrixStack, left, top, right, top + shadingHeight, 0xFF_00_00_00, 0x00_00_00_00);
		this.fillGradient(matrixStack, left, bot - shadingHeight, right, bot, 0x00_00_00_00, 0xFF_00_00_00);
	}
	
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Rectangle rect)
	{
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	private void drawSrollBar(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Rectangle rect)
	{
		int top = rect.getTop();
		
		int bot = rect.getBottom();
		int barRight = rect.getRight();
		int barLeft = barRight - this.scrollBarWidth;
		int thumbHeight = this.getScrollThumbSize(rect);
		int thumbTop = top + Math.max(0, this.getScrollDistance() * (rect.size.height - thumbHeight) / this.getMaxScroll(rect));
		
		// Scroll bar background
		fill(matrixStack, barLeft, top, barRight, bot, 0xFF_00_00_00);
		// Scroll bat thumb
		fill(matrixStack, barLeft, thumbTop, barRight, thumbTop + thumbHeight, 0xFF_80_80_80);
		fill(matrixStack, barLeft, thumbTop, barRight - 1, thumbTop + thumbHeight - 1, 0xFF_C0_C0_C0);
	}
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.checkLayout();
		
		Rectangle rect = this.getRect();
		
		if (rect.size.width > 0 && rect.size.height > 0)
		{
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawBackground(matrixStack, mouseX, mouseY, partialTicks, rect);
			
			int slotWidth = rect.size.width - this.scrollBarWidth;
			
			if (slotWidth > 0)
			{
				this.enableScissor(matrixStack, rect.x, rect.y, rect.size.width - this.scrollBarWidth, rect.size.height);
		        
				this.drawContent(matrixStack, mouseX, mouseY, partialTicks, rect);
				
				RenderSystem.disableScissor();
			}
			
			RenderSystem.disableDepthTest();
			
			if (this.getMaxScroll(rect) > 0)
			{
				this.drawSrollBar(matrixStack, mouseX, mouseY, partialTicks, rect);
			}

			this.drawForeground(matrixStack, mouseX, mouseY, partialTicks, rect);
		}
	}
	
	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state)
	{
		if (this.isPointInside(mouseX, mouseY))
			IScrollComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks, state);
	}

	protected void enableScissor(PoseStack matrixStack, int x, int y, int width, int height)
	{
		Matrix4f mat = matrixStack.last().pose();

		Vector4f topLeft = new Vector4f(x, y, 0.0f, 1.0f);
		topLeft.transform(mat);
		Vector4f size = new Vector4f(width, height, 0.0f, 0.0f);
		size.transform(mat);
		
		Minecraft mc = this.getMinecraft();
		
		double scale = mc.getWindow().getGuiScale();
		int screenHeight = mc.getWindow().getHeight();
		
		RenderSystem.enableScissor((int)(topLeft.x() * scale), screenHeight - (int)((topLeft.y() + size.y()) * scale), (int)(size.x() * scale), (int)(size.y() * scale));
	}
	
	// Narration
	
	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		this.content.updateNarration(narrationOutput);
		super.updateNarration(narrationOutput);
	}
	
}

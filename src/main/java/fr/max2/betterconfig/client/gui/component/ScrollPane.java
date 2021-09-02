package fr.max2.betterconfig.client.gui.component;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public class ScrollPane extends AbstractContainerEventHandler implements INestedGuiComponent, ILayoutManager
{
	protected final Minecraft minecraft;
	
	protected final IGuiComponent content;

	protected int w, h;
	protected boolean scrolling = false;
	private float scrollDistance = 0.0F;
	protected int scrollBarWidth = 6;
	protected int outerBorder = 1;
	protected int innerBorder = 4;

	/** The parent layout */
	private ILayoutManager layout = ILayoutManager.NONE;
	/** The x coordinate relative to the layout */
	protected int baseX;
	/** The y coordinate relative to the layout */
	protected int baseY;
	
	public ScrollPane(Minecraft minecraft, int x, int y, int w, int h, IGuiComponent content)
	{
		this.minecraft = minecraft;
		this.baseX = x;
		this.baseY = y;
		this.w = w;
		this.h = h;
		this.content = content;
		this.content.setLayoutManager(this);
	}
	
	// Layout
	
	@Override
	public List<? extends IGuiComponent> children()
	{
		return Arrays.asList(this.content);
	}
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		this.onLayoutChanged();
	}

	/** Sets the x position of this button relative to the layout position */
	public void setX(int x)
	{
		this.baseX = x;
		this.onLayoutChanged();
	}

	/** Sets the y position of this button relative to the layout position */
	public void setY(int y)
	{
		this.baseY = y;
		this.onLayoutChanged();
	}
	
	@Override
	public void marksLayoutDirty()
	{ }
	
	@Override
	public int getLayoutX()
	{
		return this.getX() + this.innerBorder;
	}
	
	@Override
	public int getLayoutY()
	{
		return this.getY() + this.innerBorder - this.getScrollDistance();
	}
	
	protected int getX()
	{
		return this.baseX + this.layout.getLayoutX();
	}
	
	protected int getY()
	{
		return this.baseY + this.layout.getLayoutY();
	}
	
	@Override
	public int getWidth()
	{
		return this.w;
	}
	
	@Override
	public int getHeight()
	{
		return this.h;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		int x = this.getX();
		int y = this.getY();
		return mouseX >= x
		    && mouseY >= y
		    && mouseX < x + this.w
		    && mouseY < y + this.h;
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
		this.scrollDistance = Mth.clamp(scroll, 0.0f, this.getMaxScroll());
		this.content.onLayoutChanged();
	}
	
	protected int getScrollDistance()
	{
		return (int)this.scrollDistance;
	}
	
	protected int getMaxScroll()
	{
		return Math.max(0, this.content.getHeight() + 2 * this.innerBorder - this.h);
	}
	
	protected float getWheelScrollFactor()
	{
		return 10.0f;
	}
	
	protected float getMouseScrollFactor()
	{
		int scrollHeight = Math.max(1, this.getMaxScroll());
		
		return -scrollHeight / (float) (this.h - this.getScrollThumbSize());
	}

	private int getScrollThumbSize()
	{
		int size = (int) ((float) (this.h * this.h) / this.content.getHeight());
		size = Mth.clamp(size, 32, this.h - this.innerBorder * 2);
		return size;
	}
	
	// Mouse handling
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollValue)
	{
		if (!this.isMouseOver(mouseX, mouseY))
			return false;
		
		if (super.mouseScrolled(mouseX, mouseY, scrollValue))
			return true;
		
		this.scroll(-(float)scrollValue * this.getWheelScrollFactor());
		return true;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		if (!this.isMouseOver(mouseX, mouseY))
		{
			this.scrolling = false;
			return false;
		}
		
		int scrollBarLeft = this.getX() + this.w - this.scrollBarWidth;
		
		if (mouseButton == 0 && mouseX >= scrollBarLeft)
		{
			// If mouse on scroll bar
			this.scrolling = true;
		}
		else
		{
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		return true;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
	{
		if (mouseButton != 0 || !this.scrolling)
			return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
		
		if (mouseY < this.getY())
		{
			this.setScrollDistance(0.0f);
		}
		else if (mouseY > this.getY() + this.h)
		{
			this.setScrollDistance(this.getMaxScroll());
		}
		else
		{
			this.scroll(-(float)dragY * this.getMouseScrollFactor());
		}
		return true;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
	{
		if (mouseButton != 0 || !this.scrolling)
			return super.mouseReleased(mouseX, mouseY, mouseButton);
		
		this.scrolling = false;
		return true;
	}
	
	// Rendering
	
	protected void drawBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		Matrix4f mat = matrixStack.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		
		int left = this.getX();
		int top = this.getY();
		int right = left + this.w - this.scrollBarWidth;
		int bot = top + this.h;
		

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.vertex(mat, left , bot, 0.0f).uv(left  / 32.0f, (bot + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(mat, right, bot, 0.0f).uv(right / 32.0f, (bot + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(mat, right, top, 0.0f).uv(right / 32.0f, (top + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(mat, left , top, 0.0f).uv(left  / 32.0f, (top + this.getScrollDistance()) / 32.0f).color(32, 32, 32, 255).endVertex();
		tessellator.end();
	}
	
	protected void drawForeground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		int left = this.getX();
		int top = this.getY();
		int right = left + this.w - this.scrollBarWidth;
		int bot = top + this.h;
		
		int shadingHeight = 4;
		
		this.fillGradient(matrixStack, left, top, right, top + shadingHeight, 0xFF_00_00_00, 0x00_00_00_00);
		this.fillGradient(matrixStack, left, bot - shadingHeight, right, bot, 0x00_00_00_00, 0xFF_00_00_00);
	}
	
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.content.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	private void drawSrollBar(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		int x = this.getX();
		int top = this.getY();
		
		int bot = top + this.h;
		int barRight = x + this.w;
		int barLeft = barRight - this.scrollBarWidth;
		int thumbHeight = this.getScrollThumbSize();
		int thumbTop = top + Math.max(0, this.getScrollDistance() * (this.h - thumbHeight) / this.getMaxScroll());
		
		// Scroll bar background
		fill(matrixStack, barLeft, top, barRight, bot, 0xFF_00_00_00);
		// Scroll bat thumb
		fill(matrixStack, barLeft, thumbTop, barRight, thumbTop + thumbHeight, 0xFF_80_80_80);
		fill(matrixStack, barLeft, thumbTop, barRight - 1, thumbTop + thumbHeight - 1, 0xFF_C0_C0_C0);
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.w > 0 && this.h > 0)
		{
			int x = this.getX();
			int y = this.getY();
			
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawBackground(matrixStack, mouseX, mouseY, partialTicks);
			
			int slotWidth = this.w - this.scrollBarWidth - this.innerBorder * 2;
			
			if (slotWidth > 0)
			{
				this.enableScissor(matrixStack, x, y, this.w - this.scrollBarWidth, this.h);
		        
				this.drawContent(matrixStack, mouseX, mouseY, partialTicks);
				
				RenderSystem.disableScissor();
			}
			
			RenderSystem.disableDepthTest();
			
			if (this.getMaxScroll() > 0)
			{
				this.drawSrollBar(matrixStack, mouseX, mouseY, partialTicks);
			}

			this.drawForeground(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	protected void enableScissor(PoseStack matrixStack, int x, int y, int width, int height)
	{
		Matrix4f mat = matrixStack.last().pose();

		Vector4f topLeft = new Vector4f(x, y, 0.0f, 1.0f);
		topLeft.transform(mat);
		Vector4f size = new Vector4f(width, height, 0.0f, 0.0f);
		size.transform(mat);
		
		double scale = this.minecraft.getWindow().getGuiScale();
		int screenHeight = this.minecraft.getWindow().getHeight();
		
		RenderSystem.enableScissor((int)(topLeft.x() * scale), screenHeight - (int)((topLeft.y() + size.y()) * scale), (int)(size.x() * scale), (int)(size.y() * scale));
	}
	
}

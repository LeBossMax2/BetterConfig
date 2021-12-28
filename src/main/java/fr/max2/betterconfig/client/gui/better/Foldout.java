package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Visibility;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigNode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The ui for a expand/collapse subsection */
public class Foldout extends CompositeComponent implements IBetterElement
{
	/** The height of the fouldout header */
	private static final int FOLDOUT_HEADER_HEIGHT = 24;
	
	public static final PropertyIdentifier<Boolean> FOLDED = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "folded"), Boolean.class);
	
	public static final StyleRule FOLDOUT_STYLE = StyleRule.when().type("better:foldout").then()
			.set(CompositeLayoutConfig.INNER_PADDING, new Padding(FOLDOUT_HEADER_HEIGHT, 0, 0, 0))
			.build();

	public static final StyleRule FOLDED_STYLE = StyleRule.when().parent().is(FOLDED).then()
			.set(ComponentLayoutConfig.VISIBILITY, Visibility.COLLAPSED)
			.build();
	
	/** The parent screen */
	private final BetterConfigScreen screen;
	
	/** The edited table */
	private final IConfigNode<?> node;
	/** The content that will be collapsed */
	private final IBetterElement content;
	/** The extra info to show on the tooltip */
	private final List<FormattedText> extraInfo = new ArrayList<>();
	
	/** {@code true} when the content is collapsed, {@code false} otherwise */
	private boolean folded = false;
	private boolean filteredOut = false;

	public Foldout(BetterConfigScreen screen, IConfigNode<?> node, IBetterElement content)
	{
		super("better:foldout");
		this.screen = screen;
		this.node = node;
		this.content = content;
		this.children.add((Component<?>)this.content);
		this.extraInfo.add(FormattedText.of(node.getName(), Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));
		this.extraInfo.addAll(node.getDisplayComment());
		this.registerProperty(FILTERED_OUT, () -> this.filteredOut);
		this.registerProperty(FOLDED, () -> this.folded);
		
		//this.config.sizeOverride.width = this.screen.width - X_PADDING - RIGHT_PADDING;
	}
	
	// Layout
	
	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		boolean matchFilter = filter.matches(this.node);
		this.filteredOut = this.content.filterElements(matchFilter ? ConfigFilter.ALL : filter);
		return this.filteredOut;
	}
	
	@Override
	public void computeLayout(Rectangle availableRect)
	{
		super.computeLayout(availableRect);
		updateTexts();
	}
	
	private void updateTexts()
	{
		this.extraInfo.clear();
		this.extraInfo.add(FormattedText.of(this.node.getName(), Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));
		this.extraInfo.addAll(this.node.getDisplayComment());
	}
	
	// Mouse interaction
	
	public void toggleFolding()
	{
		this.folded = !this.folded;
		this.layoutManager.marksLayoutDirty();
	}
	
	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		if (this.isOverHeader(mouseX, mouseY))
		{
			this.layoutManager.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			this.toggleFolding();
			state.consume();
			return;
		}
		
		super.onMouseClicked(mouseX, mouseY, button, state);
	}
	
	private boolean isOverHeader(double mouseX, double mouseY)
	{
		Rectangle rect = this.getRect();
		
		return mouseX >= rect.x
		    && mouseY >= rect.y
		    && mouseX < rect.getRight()
		    && mouseY < rect.y + FOLDOUT_HEADER_HEIGHT;
	}
	
	// Rendering
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		super.onRender(matrixStack, mouseX, mouseY, partialTicks);
		this.renderFoldoutHeader(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	protected void renderFoldoutHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		Rectangle rect = this.getRect();
		// Draw background
		fill(matrixStack, rect.x, rect.y + 2, rect.getRight(), rect.y + FOLDOUT_HEADER_HEIGHT - 2, 0xC0_33_33_33);

		// Draw foreground arrow icon
		int arrowU = this.folded ? 16 : 32;
		int arrowV = this.isOverHeader(mouseX, mouseY) ? 16 : 0;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BETTER_ICONS);
		blit(matrixStack, rect.x, rect.y + 4, arrowU, arrowV, 16, 16, 256, 256);
		
		// Draw foreground text
		Font font = this.screen.getFont(); 
		font.draw(matrixStack, this.node.getDisplayName(), rect.x + 16, rect.y + 1 + (FOLDOUT_HEADER_HEIGHT - font.lineHeight) / 2, 0xFF_FF_FF_FF);
	}
	
	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		super.onRenderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		if (this.isOverHeader(mouseX, mouseY))
		{
			Font font = this.screen.getFont();
			GuiUtils.drawHoveringText(matrixStack, this.extraInfo, mouseX, mouseY, this.screen.width, this.screen.height, 200, font);
		}
	}
	
	@Override
	public void invalidate()
	{
		this.content.invalidate();
	}
}
package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.gui.component.CycleFocusState;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.client.gui.component.widget.TextOverlay;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import fr.max2.betterconfig.client.util.GuiTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

/** The ui for a expand/collapse subsection */
public class Foldout extends CompositeComponent implements IBetterElement
{
	/** The height of the fouldout header */
	public static final int FOLDOUT_HEADER_HEIGHT = 24;

	public static final PropertyIdentifier<Boolean> FOLDED = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "folded"), Boolean.class);

	/** The parent screen */
	private final BetterConfigScreen screen;

	private final ConfigName identifier;
	/** The content that will be collapsed */
	private final IBetterElement content;
	/** The extra info to show on the tooltip */
	private final List<Component> extraInfo = new ArrayList<>();

	/** {@code true} when the content is collapsed, {@code false} otherwise */
	private boolean folded = false;
	private boolean filteredOut = false;

	public Foldout(BetterConfigScreen screen, ConfigName identifier, IBetterElement content)
	{
		super("better:foldout");
		this.screen = screen;
		this.identifier = identifier;
		this.content = content;
		this.children.add(new Header());
		this.children.add(this.content);
		this.updateTexts();
		this.registerProperty(FILTERED_OUT, () -> this.filteredOut);
		this.registerProperty(FOLDED, () -> this.folded);
	}

	// Layout

	@Override
	public boolean filterElements(ConfigFilter filter)
	{
		ConfigFilter matchFilter = filter.apply(this.identifier);
		this.filteredOut = this.content.filterElements(matchFilter);
		return this.filteredOut;
	}

	@Override
	public void computeLayout(Rectangle availableRect)
	{
		super.computeLayout(availableRect);
		this.updateTexts();
	}

	private void updateTexts()
	{
		this.extraInfo.clear();
		this.extraInfo.add(Component.literal(this.identifier.getName()).withStyle(ChatFormatting.YELLOW));
		this.extraInfo.addAll(this.identifier.getDisplayComment());
	}

	// Mouse interaction

	public void toggleFolding()
	{
		this.folded = !this.folded;
		this.layoutManager.marksLayoutDirty();
	}

	public class Header extends UnitComponent
	{
		public Header()
		{
			super("better:foldout_header");
			this.overlay = new TextOverlay(Foldout.this.screen, Foldout.this.extraInfo);
		}

		// Rendering

		@Override
		protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			this.renderFoldoutHeader(matrixStack, mouseX, mouseY, partialTicks);
		}

		protected void renderFoldoutHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			Rectangle rect = this.getRect();
			// Draw background
			fill(matrixStack, rect.x, rect.y + 2, rect.getRight(), rect.y + FOLDOUT_HEADER_HEIGHT - 2, 0xC0_33_33_33);

			// Draw foreground arrow icon
			int arrowU = Foldout.this.folded ? 16 : 32;
			int arrowV = this.isHovered() || this.hasFocus() ? 16 : 0;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
	        RenderSystem.setShaderTexture(0, Constants.BETTER_ICONS);
			blit(matrixStack, rect.x, rect.y + 4, arrowU, arrowV, 16, 16, 256, 256);

			// Draw foreground text
			Font font = Foldout.this.screen.getFont();
			font.draw(matrixStack, Foldout.this.identifier.getDisplayName(), rect.x + 16, rect.y + 1 + (FOLDOUT_HEADER_HEIGHT - font.lineHeight) / 2, 0xFF_FF_FF_FF);
		}

		// Input handling

		@Override
		protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
		{
			super.onMouseClicked(mouseX, mouseY, button, state);

			if (this.isHovered() && !state.isConsumed())
			{
				this.layoutManager.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				Foldout.this.toggleFolding();
				state.consume();
			}
		}

		@Override
		protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
		{
			super.onKeyPressed(keyCode, scanCode, modifiers, state);
			if (!this.hasFocus() || state.isConsumed())
				return;

			switch (keyCode)
			{
			case GLFW.GLFW_KEY_ENTER:
			case GLFW.GLFW_KEY_KP_ENTER:
			case GLFW.GLFW_KEY_SPACE:
				this.layoutManager.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				Foldout.this.toggleFolding();
				state.consume();
				break;
			default:
				break;
			}
		}

		@Override
		protected void onCycleFocus(boolean forward, CycleFocusState state)
		{
			this.cycleSelfFocus(state);
			super.onCycleFocus(forward, state);
		}

		// Narration

		@Override
		public void updateNarration(NarrationElementOutput narrationOutput)
		{
			narrationOutput.add(NarratedElementType.TITLE, Component.translatable(Foldout.this.folded ? GuiTexts.SECTION_TITLE_COLLAPSED : GuiTexts.SECTION_TITLE_SHOWN, Foldout.this.identifier.getDisplayName()));
			narrationOutput.add(NarratedElementType.USAGE, Component.translatable(this.hasFocus() ? GuiTexts.SECTION_USAGE_FOCUSED : GuiTexts.SECTION_USAGE_HOVERED));
			super.updateNarration(narrationOutput);
		}

	}

}
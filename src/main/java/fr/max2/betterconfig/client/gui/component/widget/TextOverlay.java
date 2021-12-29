package fr.max2.betterconfig.client.gui.component.widget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnTooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmlclient.gui.GuiUtils;

public class TextOverlay implements OnTooltip
{
	protected final Screen screen;
	protected final List<Component> text;
	
	public TextOverlay(Screen screen, List<Component> text)
	{
		this.screen = screen;
		this.text = text;
	}
	
	public TextOverlay(Screen screen, Component text)
	{
		this(screen, Arrays.asList(text));
	}

	@Override
	public void onTooltip(Button button, PoseStack matrixStack, int mouseX, int mouseY)
	{
		GuiUtils.drawHoveringText(matrixStack, this.text, mouseX, mouseY, this.screen.width, this.screen.height, 200, this.screen.getMinecraft().font);
	}
	
	@Override
	public void narrateTooltip(Consumer<Component> output)
	{
		this.text.forEach(output);
	}
	
}

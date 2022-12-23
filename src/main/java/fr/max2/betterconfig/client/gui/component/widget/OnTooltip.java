package fr.max2.betterconfig.client.gui.component.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public interface OnTooltip
{
	void onTooltip(Button button, PoseStack matrixStack, int mouseX, int mouseY);

	void narrateTooltip(Consumer<Component> output);

	public static final OnTooltip NO_TOOLTIP = new OnTooltip()
	{
		@Override
		public void onTooltip(Button button, PoseStack matrixStack, int mouseX, int mouseY)
		{ }

		@Override
		public void narrateTooltip(Consumer<Component> output)
		{ }
	};
}

package fr.max2.betterconfig.client.gui.rendering;

import java.lang.reflect.Type;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.util.ISerializableInterface;

public interface IRenderLayer extends ISerializableInterface
{
	void renderLayer(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick);
	
	public static class Serializer extends ISerializableInterface.Serializer
	{
		@Override
		protected Type getConcreteType(String operator, Type interfaceType)
		{
			return switch (operator)
			{
				case "none" -> NoRendering.class;
				case "box" -> DrawBox.class;
				default -> null;
			};
		}
	}
}

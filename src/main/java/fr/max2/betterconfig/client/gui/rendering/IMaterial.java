package fr.max2.betterconfig.client.gui.rendering;

import java.lang.reflect.Type;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.util.ISerializableInterface;

public interface IMaterial extends ISerializableInterface
{
	void renderMaterial(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick);
	
	public static class Serializer extends ISerializableInterface.Serializer
	{
		@Override
		protected Type getConcreteType(String operator, Type interfaceType)
		{
			return switch (operator)
			{
				case "color" -> ColorMaterial.class;
				case "texture" -> TextureMaterial.class;
				case "9-slice" -> NineSliceMaterial.class;
				default -> null;
			};
		}
	}
}

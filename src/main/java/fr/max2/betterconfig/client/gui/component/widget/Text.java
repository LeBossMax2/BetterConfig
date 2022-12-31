package fr.max2.betterconfig.client.gui.component.widget;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.client.gui.layout.Alignment;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class Text extends UnitComponent
{
	private final Supplier<List<Component>> text;
	// TODO [#2] Use style
	private final Alignment verticalAlignment;
	private final Alignment horizontalAlignment;
	private List<FormattedCharSequence> splitText;

	public Text(Component text, Alignment verticalAlignment, Alignment horizontalAlignment)
	{
		this(List.of(text), verticalAlignment, horizontalAlignment);
	}

	public Text(List<Component> text, Alignment verticalAlignment, Alignment horizontalAlignment)
	{
		this(() -> text, verticalAlignment, horizontalAlignment);
	}

	public Text(Supplier<List<Component>> text, Alignment verticalAlignment, Alignment horizontalAlignment)
	{
		super("text");
		this.text = text;
		this.verticalAlignment = verticalAlignment;
		this.horizontalAlignment = horizontalAlignment;
	}
	
	@Override
	public void computeLayout(Rectangle availableRect)
	{
		super.computeLayout(availableRect);
		updateTexts();
	}
	
	private void updateTexts()
	{
		Font font = this.layoutManager.getMinecraft().font;
		int width = this.getSize().width;
		this.splitText = this.text.get().stream().flatMap(cmp -> font.split(cmp, width).stream()).toList();
	}
	
	// Rendering
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		Rectangle rect = this.getRect();

		Font font = this.layoutManager.getMinecraft().font;
		int textHeight = this.splitText.size() * font.lineHeight;
		int maxWidth = this.splitText.stream().map(cs -> font.width(cs)).max(Comparator.comparingInt(v -> v)).orElse(0);
		
		int x = rect.x + this.horizontalAlignment.getOffset(rect.size.width - maxWidth);
		int y = rect.y + this.verticalAlignment.getOffset(rect.size.height - textHeight);

		int textColor = this.getStyleProperty(TEXT_COLOR);
		for(FormattedCharSequence line : this.splitText)
		{
			font.draw(matrixStack, line, x, y, textColor);
			y += font.lineHeight;
		}
	}

}

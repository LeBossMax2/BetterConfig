package fr.max2.betterconfig.client.gui.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.util.property.list.IReadableList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;

/**
 * A user interface builder that only shows the available configuration properties but doesn't allow edition
 */
public class DebugConfigGui extends UnitComponent
{
	/** The parent screen */
	private final BetterConfigScreen parent;
	/** The list of labels to display */
	private final List<String> labels;

	public DebugConfigGui(BetterConfigScreen parent, List<String> labels)
	{
		super("debug_gui");
		this.parent = parent;
		this.labels = labels;
	}
	
	// Rendering

	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		Font font = this.parent.getFont(); 
		this.parent.renderBackground(matrixStack);
		int y = 1;
		int color = this.getStyleProperty(TEXT_COLOR);
		font.draw(matrixStack, "Better Config Debug", 1, y, color);
		y += font.lineHeight + 2;
		font.draw(matrixStack, "Configs : " + this.parent.getModConfigs().stream().map(config -> "'" + config.getFileName() + "'").collect(Collectors.joining(", ")), 1, y, color);
		y += font.lineHeight + 2;
		for (String txt : this.labels)
		{
			font.draw(matrixStack, txt, 1, y, color);
			y += font.lineHeight + 2;
		}
	}

	@Override
	protected void onRenderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state)
	{ }
	
	// Narration
	
	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		this.labels.forEach(txt -> narrationOutput.add(NarratedElementType.TITLE, txt));
	}

	/**
	 * Builds the debug user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static DebugConfigGui build(BetterConfigScreen screen, IConfigTable config)
	{
		List<String> list = new ArrayList<>();
		config.getEntryValues().forEach(value -> buildTable(list, value.getName(), value));
		return new DebugConfigGui(screen, list);
	}
	
	private static void buildTable(List<String> content, String path, IConfigNode node)
	{
		if (node instanceof IConfigTable table)
		{
			table.getEntryValues().forEach(value -> buildTable(content, path + "." + value.getName(), value));
		}
		else if (node instanceof IConfigList list)
		{
			content.add(path + " : " + "LIST" + " = {");
			IReadableList<IConfigNode> values = list.getValueList();
			for (int i = 0; i < values.size(); i++)
			{
				buildTable(content, "[" + i + "]", values.get(i));
			}
			content.add("}");
		}
		else if (node instanceof IConfigPrimitive<?> primitive)
		{
			content.add(path + " : " + primitive.exploreType(ValueBuilder.INSTANCE));
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	/** The visitor to build config value widgets */
	private static enum ValueBuilder implements IConfigPrimitiveVisitor<Void, String>
	{
		INSTANCE;
		
		@Override
		public String visitBoolean(IConfigPrimitive<Boolean> property, Void param)
		{
			return "BOOL" + " = " + property.getValue();
		}
		
		@Override
		public String visitNumber(IConfigPrimitive<? extends Number> property, Void param)
		{
			return "NUMBER" + " = " + property.getValue();
		}
		
		@Override
		public String visitString(IConfigPrimitive<String> property, Void param)
		{
			return "STRING" + " = " + property.getValue();
		}
		
		@Override
		public <E extends Enum<E>> String visitEnum(IConfigPrimitive<E> property, Void param)
		{
			return "ENUM" + " = " + property.getValue();
		}
		
		@Override
		public String visitUnknown(IConfigPrimitive<?> property, Void param)
		{
			return "UNKNOWN" + " = " + property.getValue();
		}
	}
}

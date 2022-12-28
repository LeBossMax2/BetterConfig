package fr.max2.betterconfig.client.gui.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.config.value.ConfigUnknown;
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
	public static DebugConfigGui build(BetterConfigScreen screen, ConfigTable config)
	{
		List<String> list = new ArrayList<>();
		config.getEntryValues().forEach(entry -> buildTable(list, entry.key().getName(), entry.node()));
		return new DebugConfigGui(screen, list);
	}

	private static void buildTable(List<String> content, String path, IConfigNode node)
	{
		if (node instanceof ConfigTable table)
		{
			table.getEntryValues().forEach(entry -> buildTable(content, path + "." + entry.key().getName(), entry.node()));
		}
		else if (node instanceof ConfigList list)
		{
			content.add(path + " : " + "LIST" + " = {");
			IReadableList<ConfigList.Entry> values = list.getValueList();
			for (int i = 0; i < values.size(); i++)
			{
				buildTable(content, "[" + i + "]", values.get(i).node());
			}
			content.add("}");
		}
		else if (node instanceof ConfigPrimitive<?> primitive)
		{
			content.add(path + " : " + explorePrimitive(primitive));
		}
		else if (node instanceof ConfigUnknown unknown)
		{
			content.add(path + " : " + "UNKNOWN" + " = " + unknown.getValue());
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private static String explorePrimitive(ConfigPrimitive<?> property)
	{
		if (property instanceof ConfigPrimitive.Boolean boolNode)
		{
			return "BOOL" + " = " + property.getValue();
		}
		else if (property instanceof ConfigPrimitive.Number<?> numberNode)
		{
			return "NUMBER" + " = " + property.getValue();
		}
		else if (property instanceof ConfigPrimitive.String stringNode)
		{
			return "STRING" + " = " + property.getValue();
		}
		else if (property instanceof ConfigPrimitive.Enum<?> enumNode)
		{
			return "ENUM" + " = " + property.getValue();
		}
		else
		{
			return "UNKNOWN" + " = " + property.getValue();
		}
	}
}

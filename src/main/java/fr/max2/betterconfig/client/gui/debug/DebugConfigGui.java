package fr.max2.betterconfig.client.gui.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigValueVisitor;
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
		font.draw(matrixStack, "Better Config Debug", 1, y, 0xFFFFFFFF);
		y += font.lineHeight + 2;
		font.draw(matrixStack, "Configs : " + this.parent.getModConfigs().stream().map(config -> "'" + config.getFileName() + "'").collect(Collectors.joining(", ")), 1, y, 0xFFFFFFFF);
		y += font.lineHeight + 2;
		for (String txt : this.labels)
		{
			font.draw(matrixStack, txt, 1, y, 0xFFFFFFFF);
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
		config.exploreEntries(value -> value.exploreNode(new TableBuilder(list), value.getName())).forEach(v -> {});
		return new DebugConfigGui(screen, list);
	}
	
	/** The visitor to build config tables */
	private static class TableBuilder implements IConfigValueVisitor<String, Void>
	{
		private final List<String> content;

		public TableBuilder(List<String> content)
		{
			this.content = content;
		}
		
		@Override
		public Void visitTable(IConfigTable table, String path)
		{
			table.exploreEntries(value -> value.exploreNode(this, path + "." + value.getName())).forEach(v -> {});
			return null;
		}
		
		@Override
		public <T> Void visitList(IConfigList<T> list, String path)
		{
			this.content.add(path + " : " + "LIST" + " = {");
			IReadableList<IConfigNode<T>> values = list.getValueList();
			for (int i = 0; i < values.size(); i++)
			{
				values.get(i).exploreNode(this, "[" + i + "]");
			}
			this.content.add("}");
			return null;
		}
		
		@Override
		public <T> Void visitPrimitive(IConfigPrimitive<T> primitive, String path)
		{
			this.content.add(path + " : " + primitive.exploreType(ValueBuilder.INSTANCE));
			return null;
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

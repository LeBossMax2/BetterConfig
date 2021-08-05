package fr.max2.betterconfig.client.gui.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigValueVisitor;
import net.minecraft.client.gui.FontRenderer;

/**
 * A user interface builder that only shows the available configuration properties but doesn't allow edition
 */
public class DebugConfigGui  implements IGuiComponent
{
	/** The parent screen */
	private final BetterConfigScreen parent;
	/** The list of labels to display */
	private final List<String> labels;

	public DebugConfigGui(BetterConfigScreen parent, List<String> labels)
	{
		this.parent = parent;
		this.labels = labels;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		FontRenderer font = this.parent.getFont(); 
		this.parent.renderBackground(matrixStack);
		int y = 1;
		font.drawString(matrixStack, "Better Config Debug", 1, y, 0xFFFFFFFF);
		y += font.FONT_HEIGHT + 2;
		font.drawString(matrixStack, "Configs : " + this.parent.getModConfigs().stream().map(config -> "'" + config.getFileName() + "'").collect(Collectors.joining(", ")), 1, y, 0xFFFFFFFF);
		y += font.FONT_HEIGHT + 2;
		for (String txt : this.labels)
		{
			font.drawString(matrixStack, txt, 1, y, 0xFFFFFFFF);
			y += font.FONT_HEIGHT + 2;
		}
	}

	@Override
	public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }

	@Override
	public void setLayoutManager(ILayoutManager manager)
	{ }

	@Override
	public int getWidth()
	{
		return this.parent.width;
	}

	@Override
	public int getHeight()
	{
		return this.parent.height;
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
		config.exploreEntries((key, value) -> value.exploreNode(new TableBuilder(list), key.getLoc().getName())).forEach(v -> {});
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
			table.exploreEntries((key, value) -> value.exploreNode(this, path + "." + key)).forEach(v -> {});
			return null;
		}
		
		@Override
		public <T> Void visitList(IConfigList<T> list, String path)
		{
			this.content.add(path + " : " + "LIST" + " = {");
			for (int i = 0; i < list.getValueList().size(); i++)
			{
				list.getValueList().get(i).exploreNode(this, "[" + i + "]");
			}
			this.content.add("}");
			return null;
		}
		
		@Override
		public <T> Void visitProperty(IConfigPrimitive<T> property, String path)
		{
			this.content.add(path + " : " + property.exploreType(ValueBuilder.INSTANCE));
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

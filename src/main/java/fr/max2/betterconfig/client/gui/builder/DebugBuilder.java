package fr.max2.betterconfig.client.gui.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.ConfigProperty;
import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.widget.IUIElement;
import net.minecraft.client.gui.FontRenderer;

/**
 * A user interface builder that only shows the available configuration properties but doesn't allow edition
 */
public class DebugBuilder implements IConfigUIBuilder<DebugBuilder.DebugUI>
{

	@Override
	public ITableUIBuilder<DebugUI> start(BetterConfigScreen screen)
	{
		return new Table(screen, "");
	}
	
	private static class Table implements ITableUIBuilder<DebugUI>
	{
		private final BetterConfigScreen parent;
		private final String prefix;

		public Table(BetterConfigScreen parent, String prefix)
		{
			this.parent = parent;
			this.prefix = prefix;
		}

		@Override
		public ITableUIBuilder<DebugUI> subTableBuilder(String path, String comment)
		{
			return new Table(this.parent, this.prefix + path + ".");
		}

		@Override
		public IValueUIBuilder<DebugUI> tableEntryBuilder(String path, String comment)
		{
			return new Value(this.parent, this.prefix + path);
		}

		@Override
		public DebugUI buildTable(List<DebugUI> tableContent)
		{
			return new DebugUI(this.parent, tableContent.stream().flatMap(ui -> ui.labels.stream()).collect(Collectors.toList()));
		}
		
	}
	
	private static class Value implements IValueUIBuilder<DebugUI>
	{
		private final BetterConfigScreen parent;
		private final String path;

		public Value(BetterConfigScreen parent, String path)
		{
			this.parent = parent;
			this.path = path;
		}

		@Override
		public DebugUI buildBoolean(ConfigProperty<Boolean> property)
		{
			return new DebugUI(this.parent, this.path + " : " + "BOOL" + " = " + property.getValue());
		}

		@Override
		public DebugUI buildNumber(ConfigProperty<? extends Number> property)
		{
			return new DebugUI(this.parent, this.path + " : " + "NUMBER" + " = " + property.getValue());
		}

		@Override
		public DebugUI buildString(ConfigProperty<String> property)
		{
			return new DebugUI(this.parent, this.path + " : " + "STRING" + " = " + property.getValue());
		}

		@Override
		public <E extends Enum<E>> DebugUI buildEnum(ConfigProperty<E> property)
		{
			return new DebugUI(this.parent, this.path + " : " + "ENUM" + " = " + property.getValue());
		}

		@Override
		public DebugUI buildList(ConfigProperty<? extends List<?>> property)
		{
			StringBuilder str = new StringBuilder();
			List<?> value = property.getValue();
			for (int i = 0; i < value.size(); i++)
			{
				Object obj = value.get(i);
				if (str.length() != 0)
					str.append(", ");
				str.append("[" + i + "] " + Objects.toString(obj));
				
			}
			return new DebugUI(this.parent, this.path + " : " + "LIST" + " = {" + str.toString() + "}");
		}

		@Override
		public DebugUI buildUnknown(ConfigProperty<?> property)
		{
			return new DebugUI(this.parent, this.path + " : " + "UNKNOWN" + " = " + property.getValue());
		}
		
	}
	
	public static class DebugUI implements IUIElement
	{
		private final BetterConfigScreen parent;
		private final List<String> labels;

		public DebugUI(BetterConfigScreen parent, List<String> labels)
		{
			this.parent = parent;
			this.labels = labels;
		}
		
		public DebugUI(BetterConfigScreen parent, String label)
		{
			this(parent, Arrays.asList(label));
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
		
	}
}

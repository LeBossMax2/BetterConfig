package fr.max2.betterconfig.client.gui.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.IUIElement;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class DebugBuilder implements ConfigUIBuilder<DebugBuilder.DebugUI>
{

	@Override
	public TableUIBuilder<DebugUI> start(BetterConfigScreen screen)
	{
		return new Table(screen, "");
	}
	
	private static class Table implements TableUIBuilder<DebugUI>
	{
		private final BetterConfigScreen parent;
		private final String prefix;

		public Table(BetterConfigScreen parent, String prefix)
		{
			this.parent = parent;
			this.prefix = prefix;
		}

		@Override
		public TableUIBuilder<DebugUI> subTableBuilder(String path, String comment)
		{
			return new Table(this.parent, this.prefix + path + ".");
		}

		@Override
		public ValueUIBuilder<DebugUI> tableEntryBuilder(String path, String comment)
		{
			return new Value(this.parent, this.prefix + path);
		}

		@Override
		public DebugUI buildTable(List<DebugUI> tableContent)
		{
			return new DebugUI(this.parent, tableContent.stream().flatMap(ui -> ui.labels.stream()).collect(Collectors.toList()));
		}
		
	}
	
	private static class Value implements ValueUIBuilder<DebugUI>
	{
		private final BetterConfigScreen parent;
		private final String path;

		public Value(BetterConfigScreen parent, String path)
		{
			this.parent = parent;
			this.path = path;
		}

		@Override
		public DebugUI buildBoolean(ValueSpec spec, boolean value)
		{
			return new DebugUI(this.parent, this.path + " : " + "BOOL" + " = " + value);
		}

		@Override
		public DebugUI buildNumber(ValueSpec spec, Number value)
		{
			return new DebugUI(this.parent, this.path + " : " + "NUMBER" + " = " + value);
		}

		@Override
		public DebugUI buildString(ValueSpec spec, String value)
		{
			return new DebugUI(this.parent, this.path + " : " + "STRING" + " = " + value);
		}

		@Override
		public DebugUI buildEnum(ValueSpec spec, Enum<?> value)
		{
			return new DebugUI(this.parent, this.path + " : " + "ENUM" + " = " + value);
		}

		@Override
		public DebugUI buildList(ValueSpec spec, List<?> value)
		{
			StringBuilder str = new StringBuilder();
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
		public DebugUI buildUnknown(ValueSpec spec, Object value)
		{
			return new DebugUI(this.parent, this.path + " : " + "UNKNOWN" + " = " + value);
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
			this.parent.renderBackground(matrixStack);
			this.parent.getFont().drawString(matrixStack, "Better Config !", 1, 1, 0xFFFFFFFF);
			int y = 10;
			for (String txt : this.labels)
			{
				this.parent.getFont().drawString(matrixStack, txt, 1, y, 0xFFFFFFFF);
				y += 10;
			}
		}

		@Override
		public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{ }
		
	}
}

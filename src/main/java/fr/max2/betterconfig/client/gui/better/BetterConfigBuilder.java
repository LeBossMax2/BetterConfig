package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.better.widget.NumberInputField;
import fr.max2.betterconfig.client.gui.better.widget.OptionButton;
import fr.max2.betterconfig.client.gui.better.widget.StringInputField;
import fr.max2.betterconfig.client.gui.better.widget.UnknownOptionWidget;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigValueVisitor;
import net.minecraft.util.text.TranslationTextComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** A builder for better configuration screen */
public class BetterConfigBuilder implements IConfigValueVisitor<Void, IBetterElement>, IConfigPrimitiveVisitor<Void, IBetterElement>
{
	/**
	 * Builds the user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static IGuiComponent build(BetterConfigScreen screen, IConfigTable config)
	{
		return new GuiRoot(screen, BetterConfigBuilder.buildTable(screen, config, 0));
	}
	
	/** The position of the value widgets relative to the right side */
	private static final int VALUE_OFFSET = 2 * X_PADDING + RIGHT_PADDING + VALUE_WIDTH + 4 + VALUE_HEIGHT;
	
	/** The parent screen */
	private final BetterConfigScreen screen;
	private final Integer xOffset;

	private BetterConfigBuilder(BetterConfigScreen screen, Integer xOffset)
	{
		this.screen = screen;
		this.xOffset = xOffset;
	}
	
	private static IBetterElement buildTable(BetterConfigScreen screen, IConfigTable table, int xOffset)
	{
		List<IBetterElement> content = table.exploreEntries(value -> value.exploreNode(new BetterConfigBuilder(screen, xOffset))).collect(Collectors.toList());
		return new GuiGroup(screen.width - 2 * X_PADDING - RIGHT_PADDING - xOffset, content);
	}
	
	private void buildList(IConfigList<?> list, int xOffset, List<IBetterElement> content, List<ListElemInfo> entries, Runnable layoutUpdater)
	{
		int i = 0;

		List<? extends IConfigNode<?>> values = list.getValueList();
		content.add(new BetterButton(this.screen, xOffset, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH, new TranslationTextComponent(ADD_ELEMENT_KEY), thiz ->
		{
			IConfigNode<?> elem = list.addValue(0);
			putListElement(list, elem, xOffset, content, entries, layoutUpdater, 0);
			layoutUpdater.run();
		}, new TranslationTextComponent(ADD_FIRST_TOOLTIP_KEY)));
		
		for (IConfigNode<?> elem : values)
		{
			putListElement(list, elem, xOffset, content, entries, layoutUpdater, i);
			i++;
		}
		
		layoutUpdater.run();
	}
	
	private void putListElement(IConfigList<?> list, IConfigNode<?> elem, int xOffset, List<IBetterElement> content, List<ListElemInfo> entries, Runnable layoutUpdater, int index)
	{
		ListElemInfo info = new ListElemInfo(index);
		entries.add(index, info);
		boolean isFirst = content.size() <= 1;
		IBetterElement child = elem.exploreNode(new BetterConfigBuilder(this.screen, xOffset));
		content.add(index + 1, new ListElementEntry(this.screen, child, xOffset - SECTION_TAB_SIZE, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH + SECTION_TAB_SIZE, deleteButton ->
		{
			int myIndex = info.getIndex();
			list.removeValueAt(myIndex);
			content.remove(myIndex + 1);
			entries.remove(myIndex);
			for (int i = myIndex; i < entries.size(); i++)
			{
				entries.get(i).updateIndex(i);
			}
			layoutUpdater.run();
			if (content.size() == 2)
			{
				// Remove "add last" button
				content.remove(1);
			}
		}));
		for (int i = index + 1; i < entries.size(); i++)
		{
			entries.get(i).updateIndex(i);
		}
		
		if (isFirst)
		{
			// Add "add last" button
			content.add(new BetterButton(this.screen, xOffset, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH, new TranslationTextComponent(ADD_ELEMENT_KEY), thiz ->
			{
				int lastIndex = list.getValueList().size();
				IConfigNode<?> newElem = list.addValue(lastIndex);
				putListElement(list, newElem, xOffset, content, entries, layoutUpdater, lastIndex);
				layoutUpdater.run();
			}, new TranslationTextComponent(ADD_LAST_TOOLTIP_KEY)));
		}
	}
	
	// Table entry visitor
	
	@Override
	public IBetterElement visitTable(IConfigTable table, Void entry)
	{
		return new Foldout(this.screen, table, buildTable(this.screen, table, this.xOffset + SECTION_TAB_SIZE), this.xOffset);
	}
	
	@Override
	public <T> IBetterElement visitList(IConfigList<T> list, Void entry)
	{
		int offset = this.xOffset + SECTION_TAB_SIZE;
		List<IBetterElement> content = new ArrayList<>();
		GuiGroup uiGroup = new GuiGroup(this.screen.width - 2 * X_PADDING - RIGHT_PADDING - offset, content);
		this.buildList(list, offset, content, new ArrayList<>(), uiGroup::updateLayout);
		return new Foldout(this.screen, list, uiGroup, this.xOffset);
	}
	
	@Override
	public <T> IBetterElement visitPrimitive(IConfigPrimitive<T> primitive, Void entry)
	{
		IBetterElement widget = primitive.exploreType(this, entry);
		return new ValueEntry(this.screen, primitive, widget, this.xOffset);
	}
	
	// Property visitor
	
	@Override
	public IBetterElement visitBoolean(IConfigPrimitive<Boolean> property, Void entry)
	{
		return OptionButton.booleanOption(this.screen.width - VALUE_OFFSET, property);
	}
	
	@Override
	public IBetterElement visitNumber(IConfigPrimitive<? extends Number> property, Void entry)
	{
		return NumberInputField.numberOption(this.screen, this.screen.width - VALUE_OFFSET, property);
	}
	
	@Override
	public IBetterElement visitString(IConfigPrimitive<String> property, Void entry)
	{
		return StringInputField.stringOption(this.screen, this.screen.width - VALUE_OFFSET, property);
	}
	
	@Override
	public <E extends Enum<E>> IBetterElement visitEnum(IConfigPrimitive<E> property, Void entry)
	{
		return OptionButton.enumOption(this.screen.width - VALUE_OFFSET, property);
	}
	
	@Override
	public IBetterElement visitUnknown(IConfigPrimitive<?> property, Void entry)
	{
		return new UnknownOptionWidget(this.screen.width - VALUE_OFFSET, property);
	}
	
	private static class ListElemInfo
	{
		private int index; //TODO [1.0] Find an alternative to this int holder class
		
		public ListElemInfo(int index)
		{
			this.index = index;
		}
		
		public void updateIndex(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return this.index;
		}
	}
}

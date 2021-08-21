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
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
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
	private final int xOffset;

	private BetterConfigBuilder(BetterConfigScreen screen, int xOffset)
	{
		this.screen = screen;
		this.xOffset = xOffset;
	}
	
	private static IBetterElement buildTable(BetterConfigScreen screen, IConfigTable table, int xOffset)
	{
		List<IBetterElement> content = table.exploreEntries(value -> value.exploreNode(new BetterConfigBuilder(screen, xOffset))).collect(Collectors.toList());
		return new GuiGroup(screen.width - 2 * X_PADDING - RIGHT_PADDING - xOffset, content);
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
		List<IBetterElement> mainElements = new ArrayList<>();
		GuiGroup mainGroup = new GuiGroup(this.screen.width - 2 * X_PADDING - RIGHT_PADDING - offset, mainElements);
		
		IReadableList<IConfigNode<T>> values = list.getValueList();
		mainElements.add(new BetterButton(this.screen, offset, this.screen.width - offset - VALUE_OFFSET + VALUE_WIDTH, new TranslationTextComponent(ADD_ELEMENT_KEY), thiz ->
		{
			list.addValue(0);
		}, new TranslationTextComponent(ADD_FIRST_TOOLTIP_KEY)));
		
		List<ListElemInfo> entries = new ArrayList<>();
		IReadableList<IBetterElement> content = values.derived((index, elem) -> this.buildListElementGui(list, elem, offset, mainElements, entries, index));
		mainElements.add(1, new GuiGroup(this.screen.width - 2 * X_PADDING - RIGHT_PADDING - offset, content));
		
		content.onChanged(new IListListener<IBetterElement>()
		{
			@Override
			public void onElementAdded(int index, IBetterElement newValue)
			{
				for (int i = index + 1; i < entries.size(); i++)
				{
					entries.get(i).updateIndex(i);
				}
				
				if (entries.size() == 1)
					mainElements.add(buildAddLastButton(list, offset));
				
				mainGroup.updateLayout();
			}

			@Override
			public void onElementRemoved(int index)
			{
				for (int i = index; i < entries.size(); i++)
				{
					entries.get(i).updateIndex(i);
				}
				
				if (entries.size() == 0)
					mainElements.remove(2); // Remove "add last" button
				
				mainGroup.updateLayout();
			}
		});

		
		if (entries.size() >= 1)
			mainElements.add(buildAddLastButton(list, offset));
		
		mainGroup.updateLayout();
		
		return new Foldout(this.screen, list, mainGroup, this.xOffset);
	}

	private <T> BetterButton buildAddLastButton(IConfigList<T> list, int offset)
	{
		return new BetterButton(screen, offset, screen.width - offset - VALUE_OFFSET + VALUE_WIDTH, new TranslationTextComponent(ADD_ELEMENT_KEY), thiz ->
		{
			list.addValue(list.getValueList().size());
		}, new TranslationTextComponent(ADD_LAST_TOOLTIP_KEY));
	}
	
	private IBetterElement buildListElementGui(IConfigList<?> list, IConfigNode<?> elem, int xOffset, List<IBetterElement> content, List<ListElemInfo> entries, int index)
	{
		ListElemInfo info = new ListElemInfo(index);
		entries.add(index, info);
		IBetterElement child = elem.exploreNode(new BetterConfigBuilder(this.screen, xOffset));
		
		return new ListElementEntry(this.screen, child, xOffset - SECTION_TAB_SIZE, this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH + SECTION_TAB_SIZE, deleteButton ->
		{
			int myIndex = info.getIndex();
			entries.remove(myIndex);
			list.removeValueAt(myIndex);
		});
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
		//TODO [#1] Make displayed value update when config value change 
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
		private int index; //TODO [#2] Find an alternative to this int holder class
		
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

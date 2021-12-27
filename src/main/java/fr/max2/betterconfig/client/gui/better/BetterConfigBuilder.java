package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.better.widget.NumberInputField;
import fr.max2.betterconfig.client.gui.better.widget.OptionButton;
import fr.max2.betterconfig.client.gui.better.widget.StringInputField;
import fr.max2.betterconfig.client.gui.better.widget.UnknownOptionWidget;
import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigValueVisitor;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import net.minecraft.network.chat.TranslatableComponent;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** A builder for better configuration screen */
public class BetterConfigBuilder implements IConfigValueVisitor<Void, IBetterElement>, IConfigPrimitiveVisitor<Void, IComponent>
{
	public static final StyleRule ROOT_STYLE = StyleRule.when().contains(Component.COMPONENT_CLASSES, "better:root_group").then()
			.set(ComponentLayoutConfig.OUTER_PADDING, new Padding(6, 6 + 6, 6, 6))
			.build();
	public static final StyleRule TABLE_STYLE = StyleRule.when().contains(Component.COMPONENT_CLASSES, "better:table_group").then()
			.set(ComponentLayoutConfig.OUTER_PADDING, new Padding(0, 0, 0, SECTION_TAB_SIZE))
			.build();
	public static final StyleRule LIST_STYLE = StyleRule.when().contains(Component.COMPONENT_CLASSES, "better:list_group").then()
			.set(ComponentLayoutConfig.OUTER_PADDING, new Padding(0, 0, 0, SECTION_TAB_SIZE))
			.build();
	
	/**
	 * Builds the user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static IComponent build(BetterConfigScreen screen, IConfigTable config)
	{
		GuiGroup tableGroup = new BetterConfigBuilder(screen).buildTable(config);
		tableGroup.addClass("better:table_group");
		tableGroup.addClass("better:root_group");
		
		return new GuiRoot(screen, tableGroup);
	}
	
	// private static final int VALUE_OFFSET = 2 * X_PADDING + RIGHT_PADDING + VALUE_WIDTH + 4 + VALUE_HEIGHT;
	
	/** The parent screen */
	private final BetterConfigScreen screen;

	private BetterConfigBuilder(BetterConfigScreen screen)
	{
		this.screen = screen;
	}
	
	private GuiGroup buildTable(IConfigTable table)
	{
		List<IBetterElement> content = table.exploreEntries(value -> value.exploreNode(this)).collect(Collectors.toList());
		return new GuiGroup(content);
		// config.width = this.screen.width - 2 * X_PADDING - RIGHT_PADDING - xOffset
	}
	
	// Table entry visitor
	
	@Override
	public IBetterElement visitTable(IConfigTable table, Void entry)
	{
		GuiGroup tableGroup = this.buildTable(table);
		tableGroup.addClass("better:table_group");
		return new Foldout(this.screen, table, tableGroup);
	}
	
	@Override
	public <T> IBetterElement visitList(IConfigList<T> list, Void entry)
	{
		IReadableList<IComponent> mainElements = new ObservableList<>();
		GuiGroup mainGroup = new GuiGroup(mainElements);
		mainGroup.addClass("better:list_group");
		
		IReadableList<IConfigNode<T>> values = list.getValueList();
		mainElements.add(new BetterButton(this.screen, Size.UNCONSTRAINED, new TranslatableComponent(ADD_ELEMENT_KEY), thiz ->
		{
			list.addValue(0);
		}, new TranslatableComponent(ADD_FIRST_TOOLTIP_KEY)));
		
		List<ListElemInfo> entries = new ArrayList<>();
		IReadableList<IBetterElement> content = values.derived((index, elem) -> this.buildListElementGui(list, elem, entries, index));
		IListListener<IBetterElement> listListener = new IListListener<>()
		{
			@Override
			public void onElementAdded(int index, IBetterElement newValue)
			{
				for (int i = index + 1; i < entries.size(); i++)
				{
					entries.get(i).updateIndex(i);
				}
				
				if (entries.size() == 1)
					mainElements.add(buildAddLastButton(list));
				
				mainGroup.updateLayout();
			}

			@Override
			public void onElementRemoved(int index, IBetterElement oldValue)
			{
				oldValue.invalidate();
				for (int i = index; i < entries.size(); i++)
				{
					entries.get(i).updateIndex(i);
				}
				
				if (entries.size() == 0)
					mainElements.remove(2); // Remove "add last" button
				
				mainGroup.updateLayout();
			}
		};
		mainElements.add(1, new GuiGroup(content)
		{
			@Override
			public void invalidate()
			{
				super.invalidate();
				content.removeOnChangedListener(listListener);
			}
		});
		// config.width = this.screen.width - 2 * X_PADDING - RIGHT_PADDING - offset
		
		content.onChanged(listListener);

		
		if (entries.size() >= 1)
			mainElements.add(buildAddLastButton(list));
		
		return new Foldout(this.screen, list, mainGroup);
	}

	private <T> BetterButton buildAddLastButton(IConfigList<T> list)
	{
		return new BetterButton(this.screen, Size.UNCONSTRAINED, new TranslatableComponent(ADD_ELEMENT_KEY), thiz ->
		{
			list.addValue(list.getValueList().size());
		}, new TranslatableComponent(ADD_LAST_TOOLTIP_KEY));
	}
	
	private IBetterElement buildListElementGui(IConfigList<?> list, IConfigNode<?> elem, List<ListElemInfo> entries, int index)
	{
		ListElemInfo info = new ListElemInfo(index);
		entries.add(index, info);
		IBetterElement child = elem.exploreNode(this);
		
		return new ListElementEntry(this.screen, child, deleteButton ->
		{
			int myIndex = info.getIndex();
			entries.remove(myIndex);
			list.removeValueAt(myIndex);
		});
		// x = xOffset - SECTION_TAB_SIZE;
		// width = this.screen.width - xOffset - VALUE_OFFSET + VALUE_WIDTH + SECTION_TAB_SIZE
	}
	
	@Override
	public <T> IBetterElement visitPrimitive(IConfigPrimitive<T> primitive, Void entry)
	{
		IComponent widget = primitive.exploreType(this, entry);
		return new ValueEntry(this.screen, primitive, widget);
	}
	
	// Property visitor
	
	@Override
	public IComponent visitBoolean(IConfigPrimitive<Boolean> property, Void entry)
	{
		return OptionButton.booleanOption(property);
	}
	
	@Override
	public IComponent visitNumber(IConfigPrimitive<? extends Number> property, Void entry)
	{
		return NumberInputField.numberOption(this.screen, property);
	}
	
	@Override
	public IComponent visitString(IConfigPrimitive<String> property, Void entry)
	{
		return StringInputField.stringOption(this.screen, property);
	}
	
	@Override
	public <E extends Enum<E>> IComponent visitEnum(IConfigPrimitive<E> property, Void entry)
	{
		return OptionButton.enumOption(property);
	}
	
	@Override
	public IComponent visitUnknown(IConfigPrimitive<?> property, Void entry)
	{
		return new UnknownOptionWidget(property);
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

package fr.max2.betterconfig.client.gui.better;

import java.util.List;
import java.util.stream.Collectors;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.better.widget.NumberInputField;
import fr.max2.betterconfig.client.gui.better.widget.OptionButton;
import fr.max2.betterconfig.client.gui.better.widget.StringInputField;
import fr.max2.betterconfig.client.gui.better.widget.UnknownOptionWidget;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigValueVisitor;
import fr.max2.betterconfig.util.property.list.IIndexedProperty;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import net.minecraft.network.chat.TranslatableComponent;


/** A builder for better configuration screen */
public class BetterConfigBuilder implements IConfigValueVisitor<Void, IBetterElement>, IConfigPrimitiveVisitor<Void, IComponent>
{
	/**
	 * Builds the user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static IComponent build(BetterConfigScreen screen, IConfigTable config)
	{
		GuiGroup tableGroup = new BetterConfigBuilder(screen).buildTable(config);
		tableGroup.addClass("better:root_group");
		
		return new GuiRoot(screen, tableGroup);
	}
	
	/** The parent screen */
	private final BetterConfigScreen screen;

	private BetterConfigBuilder(BetterConfigScreen screen)
	{
		this.screen = screen;
	}
	
	private GuiGroup buildTable(IConfigTable table)
	{
		List<IBetterElement> content = table.exploreEntries(value -> value.exploreNode(this)).collect(Collectors.toList());
		GuiGroup tableGroup = new GuiGroup(content);
		tableGroup.addClass("better:table_group");
		return tableGroup;
	}
	
	// Table entry visitor
	
	@Override
	public IBetterElement visitTable(IConfigTable table, Void entry)
	{
		return new Foldout(this.screen, table, this.buildTable(table));
	}
	
	@Override
	public <T> IBetterElement visitList(IConfigList<T> list, Void entry)
	{
		IReadableList<IComponent> mainElements = new ObservableList<>();
		GuiGroup mainGroup = new GuiGroup(mainElements);
		mainGroup.addClass("better:list_group");
		
		IReadableList<IConfigNode<T>> values = list.getValueList();
		mainElements.add(new BetterButton(this.screen, new TranslatableComponent(GuiTexts.ADD_ELEMENT_KEY), thiz ->
		{
			list.addValue(0);
		}, new TranslatableComponent(GuiTexts.ADD_FIRST_TOOLTIP_KEY)));
		
		IReadableList<IBetterElement> content = values.derived((index, elem) -> this.buildListElementGui(list, elem, values.getIndexedProperties().get(index)));
		IListListener<IBetterElement> listListener = new IListListener<>()
		{
			@Override
			public void onElementAdded(int index, IBetterElement newValue)
			{
				if (content.size() == 1)
					mainElements.add(buildAddLastButton(list));
				
				mainGroup.updateLayout();
			}

			@Override
			public void onElementRemoved(int index, IBetterElement oldValue)
			{
				oldValue.invalidate();
				
				if (content.size() == 0)
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
		
		content.onChanged(listListener);

		
		if (content.size() >= 1)
			mainElements.add(buildAddLastButton(list));
		
		return new Foldout(this.screen, list, mainGroup);
	}

	private <T> BetterButton buildAddLastButton(IConfigList<T> list)
	{
		return new BetterButton(this.screen, new TranslatableComponent(GuiTexts.ADD_ELEMENT_KEY), thiz ->
		{
			list.addValue(list.getValueList().size());
		}, new TranslatableComponent(GuiTexts.ADD_LAST_TOOLTIP_KEY));
	}
	
	private IBetterElement buildListElementGui(IConfigList<?> list, IConfigNode<?> elem, IIndexedProperty<?> entry)
	{
		IBetterElement child = elem.exploreNode(this);
		
		return new ListElementEntry(this.screen, child, deleteButton ->
		{
			list.removeValueAt(entry.getIndex());
		});
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
}

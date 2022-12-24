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
import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.value.IConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.IConfigTable;
import fr.max2.betterconfig.util.property.list.IIndexedProperty;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import net.minecraft.network.chat.TranslatableComponent;


/** A builder for better configuration screen */
public class BetterConfigBuilder implements IConfigPrimitiveVisitor<IConfigName, IComponent>
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
		List<IBetterElement> content = table.getEntryValues().stream().map(entry -> this.visitNode(entry.key(), entry.node())).collect(Collectors.toList());
		GuiGroup tableGroup = new GuiGroup(content);
		tableGroup.addClass("better:table_group");
		return tableGroup;
	}
	
	// Table entry visitor
	
	private IBetterElement visitNode(IConfigName identifier, IConfigNode node)
	{
		if (node instanceof IConfigTable table)
		{
			return visitTable(identifier, table);
		}
		else if (node instanceof IConfigList list)
		{
			return this.visitList(identifier, list);
		}
		else if (node instanceof IConfigPrimitive<?> primitive)
		{
			return this.visitPrimitive(identifier, primitive);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	
	private IBetterElement visitTable(IConfigName identifier, IConfigTable table)
	{
		return new Foldout(this.screen, identifier, this.buildTable(table));
	}
	
	private IBetterElement visitList(IConfigName identifier, IConfigList list)
	{
		IReadableList<IComponent> mainElements = new ObservableList<>();
		GuiGroup mainGroup = new GuiGroup(mainElements);
		mainGroup.addClass("better:list_group");
		
		IReadableList<IConfigList.Entry> values = list.getValueList();
		mainElements.add(new BetterButton(this.screen, new TranslatableComponent(GuiTexts.ADD_ELEMENT_KEY), new TranslatableComponent(GuiTexts.ADD_FIRST_TOOLTIP_KEY))
				.addOnPressed(() -> list.addValue(0)));
		
		IReadableList<IBetterElement> content = values.derived((index, elem) -> this.buildListElementGui(list, elem.key(), elem.node(), values.getIndexedProperties().get(index)));
		IListListener<IBetterElement> listListener = new IListListener<>()
		{
			@Override
			public void onElementAdded(int index, IBetterElement newValue)
			{
				if (content.size() == 1)
					mainElements.add(buildAddLastButton(list)); // Add "add last" button
			}

			@Override
			public void onElementRemoved(int index, IBetterElement oldValue)
			{
				oldValue.invalidate();
				
				if (content.size() == 0)
					mainElements.remove(2); // Remove "add last" button
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
		
		return new Foldout(this.screen, identifier, mainGroup);
	}

	private BetterButton buildAddLastButton(IConfigList list)
	{
		BetterButton button = new BetterButton(this.screen, new TranslatableComponent(GuiTexts.ADD_ELEMENT_KEY), new TranslatableComponent(GuiTexts.ADD_LAST_TOOLTIP_KEY));
		button.addOnPressed(() -> list.addValue(list.getValueList().size()));
		return button;
	}
	
	private IBetterElement buildListElementGui(IConfigList list, IConfigName identifier, IConfigNode elem, IIndexedProperty<?> entry)
	{
		IBetterElement child = this.visitNode(identifier, elem);
		
		return new ListElementEntry(this.screen, child, () -> list.removeValueAt(entry.getIndex()));
	}
	
	private <T> IBetterElement visitPrimitive(IConfigName identifier, IConfigPrimitive<T> primitive)
	{
		IComponent widget = primitive.exploreType(this, identifier);
		return new ValueEntry(this.screen, identifier, primitive, widget);
	}
	
	// Property visitor
	
	@Override
	public IComponent visitBoolean(IConfigPrimitive<Boolean> property, IConfigName identifier)
	{
		return OptionButton.booleanOption(property);
	}
	
	@Override
	public IComponent visitNumber(IConfigPrimitive<? extends Number> property, IConfigName identifier)
	{
		return NumberInputField.numberOption(this.screen, identifier, property);
	}
	
	@Override
	public IComponent visitString(IConfigPrimitive<String> property, IConfigName identifier)
	{
		return StringInputField.stringOption(this.screen, identifier, property);
	}
	
	@Override
	public <E extends Enum<E>> IComponent visitEnum(IConfigPrimitive<E> property, IConfigName identifier)
	{
		return OptionButton.enumOption(property);
	}
	
	@Override
	public IComponent visitUnknown(IConfigPrimitive<?> property, IConfigName identifier)
	{
		return new UnknownOptionWidget(property);
	}
}

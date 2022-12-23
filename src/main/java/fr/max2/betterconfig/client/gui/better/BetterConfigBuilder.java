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
import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.IConfigPrimitiveVisitor;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.util.property.list.IIndexedProperty;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import net.minecraft.network.chat.Component;


/** A builder for better configuration screen */
public class BetterConfigBuilder implements IConfigPrimitiveVisitor<IConfigName, IComponent>
{
	/**
	 * Builds the user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static IComponent build(BetterConfigScreen screen, ConfigTable config)
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
	
	private GuiGroup buildTable(ConfigTable table)
	{
		List<IBetterElement> content = table.getEntryValues().stream().map(entry -> this.visitNode(entry.key(), entry.node())).collect(Collectors.toList());
		GuiGroup tableGroup = new GuiGroup(content);
		tableGroup.addClass("better:table_group");
		return tableGroup;
	}

	// Table entry visitor
	
	private IBetterElement visitNode(IConfigName identifier, IConfigNode node)
	{
		if (node instanceof ConfigTable table)
		{
			return visitTable(identifier, table);
		}
		else if (node instanceof ConfigList list)
		{
			return this.visitList(identifier, list);
		}
		else if (node instanceof ConfigPrimitive<?> primitive)
		{
			return this.visitPrimitive(identifier, primitive);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	
	private IBetterElement visitTable(IConfigName identifier, ConfigTable table)
	{
		return new Foldout(this.screen, identifier, this.buildTable(table));
	}
	
	private IBetterElement visitList(IConfigName identifier, ConfigList list)
	{
		IReadableList<IComponent> mainElements = new ObservableList<>();
		GuiGroup mainGroup = new GuiGroup(mainElements);
		mainGroup.addClass("better:list_group");
		
		IReadableList<ConfigList.Entry> values = list.getValueList();
		mainElements.add(new BetterButton(this.screen, Component.translatable(GuiTexts.ADD_ELEMENT_KEY), Component.translatable(GuiTexts.ADD_FIRST_TOOLTIP_KEY))
				.addOnPressed(() -> list.addValue(0)));
		
		IReadableList<IBetterElement> content = values.derived((index, elem) -> this.buildListElementGui(list, elem.key(), elem.node(), values.getIndexedProperties().get(index)));
		IListListener<IBetterElement> listListener = new IListListener<>()
		{
			@Override
			public void onElementAdded(int index, IBetterElement newValue)
			{
				if (content.size() == 1)
					mainElements.add(BetterConfigBuilder.this.buildAddLastButton(list)); // Add "add last" button
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
			mainElements.add(this.buildAddLastButton(list));
		
		return new Foldout(this.screen, identifier, mainGroup);
	}

	private BetterButton buildAddLastButton(ConfigList list)
	{
		BetterButton button = new BetterButton(this.screen, Component.translatable(GuiTexts.ADD_ELEMENT_KEY), Component.translatable(GuiTexts.ADD_LAST_TOOLTIP_KEY));
		button.addOnPressed(() -> list.addValue(list.getValueList().size()));
		return button;
	}
	
	private IBetterElement buildListElementGui(ConfigList list, IConfigName identifier, IConfigNode elem, IIndexedProperty<?> entry)
	{
		IBetterElement child = this.visitNode(identifier, elem);
		
		return new ListElementEntry(this.screen, child, () -> list.removeValueAt(entry.getIndex()));
	}
	
	private <T> IBetterElement visitPrimitive(IConfigName identifier, ConfigPrimitive<T> primitive)
	{
		IComponent widget = primitive.exploreType(this, identifier);
		return new ValueEntry(this.screen, identifier, primitive, widget);
	}

	// Property visitor

	@Override
	public IComponent visitBoolean(ConfigPrimitive<Boolean> property, IConfigName identifier)
	{
		return OptionButton.booleanOption(property);
	}

	@Override
	public IComponent visitNumber(ConfigPrimitive<? extends Number> property, IConfigName identifier)
	{
		return NumberInputField.numberOption(this.screen, identifier, property);
	}

	@Override
	public IComponent visitString(ConfigPrimitive<String> property, IConfigName identifier)
	{
		return StringInputField.stringOption(this.screen, identifier, property);
	}

	@Override
	public <E extends Enum<E>> IComponent visitEnum(ConfigPrimitive<E> property, IConfigName identifier)
	{
		return OptionButton.enumOption(property);
	}

	@Override
	public IComponent visitUnknown(ConfigPrimitive<?> property, IConfigName identifier)
	{
		return new UnknownOptionWidget(property);
	}
}

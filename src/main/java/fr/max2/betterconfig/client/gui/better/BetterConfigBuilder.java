package fr.max2.betterconfig.client.gui.better;

import java.util.List;
import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.better.widget.NumberInputField;
import fr.max2.betterconfig.client.gui.better.widget.OptionButton;
import fr.max2.betterconfig.client.gui.better.widget.StringInputField;
import fr.max2.betterconfig.client.gui.better.widget.UnknownOptionWidget;
import fr.max2.betterconfig.client.gui.component.IComponent;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.ConfigTableKey;
import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.ConfigNode;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.config.value.ConfigUnknown;
import fr.max2.betterconfig.util.property.list.IListListener;
import fr.max2.betterconfig.util.property.list.IReadableList;
import fr.max2.betterconfig.util.property.list.ObservableList;
import net.minecraft.network.chat.Component;


/** A builder for better configuration screen */
public class BetterConfigBuilder
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
		List<IBetterElement> content = table.getEntryValues().stream().map(entry -> this.visitNode(new TableChildInfo(entry.key()), entry.node())).toList();
		GuiGroup tableGroup = new GuiGroup(content);
		tableGroup.addClass("better:table_group");
		return tableGroup;
	}

	// Table entry visitor

	private IBetterElement visitNode(ConfigName identifier, ConfigNode node)
	{
		if (node instanceof ConfigTable table)
		{
			return this.visitTable(identifier, table);
		}
		else if (node instanceof ConfigList list)
		{
			return this.visitList(identifier, list);
		}
		else if (node instanceof ConfigPrimitive<?> primitive)
		{
			return this.visitPrimitive(identifier, primitive);
		}
		else if (node instanceof ConfigUnknown unknown)
		{
			return new ValueEntry(this.screen, identifier, unknown, new UnknownOptionWidget(unknown));
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}


	private IBetterElement visitTable(ConfigName identifier, ConfigTable table)
	{
		return new Foldout(this.screen, identifier, this.buildTable(table));
	}

	private IBetterElement visitList(ConfigName identifier, ConfigList list)
	{
		IReadableList<IComponent> mainElements = new ObservableList<>();
		GuiGroup mainGroup = new GuiGroup(mainElements);
		mainGroup.addClass("better:list_group");

		mainElements.add(new BetterButton(this.screen, Component.translatable(GuiTexts.ADD_ELEMENT_KEY), Component.translatable(GuiTexts.ADD_FIRST_TOOLTIP_KEY))
				.addOnPressed(() -> list.addValue(0)));

		IReadableList<IBetterElement> content = list.getValueList().derived(elem -> this.buildListElementGui(list, new ListChildInfo(identifier, elem.index()), elem.node(), elem.index()));
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
				content.onChanged().remove(listListener);
			}
		});

		content.onChanged().add(listListener);


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

	private IBetterElement buildListElementGui(ConfigList list, ConfigName identifier, ConfigNode elem, ConfigList.Index index)
	{
		IBetterElement child = this.visitNode(identifier, elem);

		return new ListElementEntry(this.screen, child, () -> list.removeValueAt(index.get()));
	}

	private <T> IBetterElement visitPrimitive(ConfigName identifier, ConfigPrimitive<T> primitive)
	{
		IComponent widget;
		if (primitive instanceof ConfigPrimitive.Boolean boolNode)
		{
			widget = OptionButton.booleanOption(boolNode);
		}
		else if (primitive instanceof ConfigPrimitive.Number<?> numberNode)
		{
			widget = NumberInputField.numberOption(this.screen, identifier, numberNode);
		}
		else if (primitive instanceof ConfigPrimitive.String stringNode)
		{
			widget = StringInputField.stringOption(this.screen, identifier, stringNode);
		}
		else if (primitive instanceof ConfigPrimitive.Enum<?> enumNode)
		{
			widget = OptionButton.enumOption(enumNode);
		}
		else
		{
			widget = new UnknownOptionWidget(primitive);
		}

		return new ValueEntry(this.screen, identifier, primitive, widget);
	}
}

class ListChildInfo implements ConfigName
{
	private final ConfigName parent;
	private final ConfigList.Index index;

	public ListChildInfo(ConfigName parent, ConfigList.Index index)
	{
		this.parent = parent;
		this.index = index;
	}

	@Override
	public String getName()
	{
		return this.parent.getName() + "[" + this.index.get() + "]";
	}

	@Override
	public Component getDisplayName()
	{
		return Component.translatable(GuiTexts.LIST_ELEMENT_LABEL_KEY, this.parent.getName(), this.index.get());
	}

	@Override
	public String getCommentString()
	{
		return this.parent.getCommentString();
	}

	@Override
	public List<? extends Component> getDisplayComment()
	{
		return this.parent.getDisplayComment();
	}
}

class TableChildInfo implements ConfigName
{
	private final ConfigTableKey entry;

	public TableChildInfo(ConfigTableKey entry)
	{
		this.entry = entry;
	}

	@Override
	public String getName()
	{
		return this.entry.getName();
	}

	@Override
	public Component getDisplayName()
	{
		return this.entry.getDisplayName();
	}

	@Override
	public String getCommentString()
	{
		return this.entry.getCommentString();
	}

	@Override
	public List<? extends Component> getDisplayComment()
	{
		return this.entry.getDisplayComment();
	}
}

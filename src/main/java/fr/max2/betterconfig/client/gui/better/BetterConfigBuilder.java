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
import fr.max2.betterconfig.util.property.list.DerivedList;
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
		List<IBetterElement> content = table.entries()
			.stream()
			.map(entry -> this.buildEntry(new TableChildInfo(entry.key()), entry.node()))
			.toList();
		GuiGroup tableGroup = new GuiGroup(content);
		tableGroup.addClass("better:table_group");
		return tableGroup;
	}

	// Table entry visitor

	private IBetterElement buildEntry(ConfigName identifier, ConfigNode node)
	{
		if (node instanceof ConfigTable table)
		{
			return new Foldout(this.screen, identifier, this.buildTable(table));
		}
		else if (node instanceof ConfigList list)
		{
			return this.buildEntryList(identifier, list);
		}
		else if (node instanceof ConfigPrimitive<?> primitive)
		{
			return this.buildEntryPrimitive(identifier, primitive);
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

	private IBetterElement buildEntryList(ConfigName identifier, ConfigList list)
	{
		DerivedList<?, IBetterElement> content = list.getValueList().derived(elem -> this.buildListElementGui(list, new ListChildInfo(identifier, elem.index()), elem.node(), elem.index()));

		var contentGroup = new GuiGroup(content)
		{
			public void invalidate()
			{
				super.invalidate();
				content.close();
			};
		};

		return new Foldout(this.screen, identifier, new ListGroup(this.screen, contentGroup, list));
	}

	private IBetterElement buildListElementGui(ConfigList list, ConfigName identifier, ConfigNode elem, ConfigList.Index index)
	{
		IBetterElement child = this.buildEntry(identifier, elem);

		return new ListElementEntry(this.screen, child, () -> list.removeValueAt(index.get()));
	}

	private <T> IBetterElement buildEntryPrimitive(ConfigName identifier, ConfigPrimitive<T> primitive)
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

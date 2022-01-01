package fr.max2.betterconfig.data;

import static fr.max2.betterconfig.client.gui.better.Constants.*;
import static fr.max2.betterconfig.client.gui.style.StyleRule.when;
import static fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig.*;
import static fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig.*;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.client.gui.better.Constants;
import fr.max2.betterconfig.client.gui.better.Foldout;
import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.component.Component;
import fr.max2.betterconfig.client.gui.component.widget.NumberField;
import fr.max2.betterconfig.client.gui.layout.Alignment;
import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.layout.Visibility;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.client.gui.style.StyleSerializer;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ModStyleSheetProvider implements DataProvider
{
	public static Gson GSON = StyleSerializer.INSTANCE.registerSerializers(new GsonBuilder()).registerTypeAdapter(Padding.class, Padding.Serializer.INSTANCE).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator generator;
	
	public ModStyleSheetProvider(DataGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public void run(HashCache pCache) throws IOException
	{
		extracted(pCache, DefaultStyleSheet.builder(), StyleSheet.DEFAULT_STYLESHEET);
	}

	private void extracted(HashCache pCache, StyleSheet.Builder styleSheet, ResourceLocation styleSheetId) throws IOException
	{
		DataProvider.save(GSON, pCache, GSON.toJsonTree(styleSheet), this.generator.getOutputFolder().resolve(PackType.CLIENT_RESOURCES.getDirectory() + "/" + styleSheetId.getNamespace() + "/" + StyleSheet.STYLESHEET_DIR + "/" + styleSheetId.getPath() + ".json"));
	}

	@Override
	public String getName()
	{
		return "StyleSheets: BetterConfig";
	}
	
	private static final class DefaultStyleSheet
	{
		/** The width of the indentation added for each nested section */
		private static final int SECTION_TAB_SIZE = 22;
		/** The height of the value entries */
		public static final int VALUE_CONTAINER_HEIGHT = 24;
		/** The x position of the input field of the search bar */
		private static final int SEARCH_LABEL_WIDTH = 80;

		private static final StyleRule BETTER_NUMBER_FIELD_STYLE = when().hasClass("better:number_field").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();
		private static final StyleRule BETTER_NUMBER_FIELD_MINUS_STYLE = when().hasClass("number_field:minus_button").parent().hasClass("better:number_field").then()
				.set(SIZE_OVERRIDE, new Size(NumberField.BUTTON_SIZE, VALUE_HEIGHT))
				.build();
		private static final StyleRule BETTER_NUMBER_FIELD_PLUS_STYLE = when().hasClass("number_field:plus_button").parent().hasClass("better:number_field").then()
				.set(SIZE_OVERRIDE, new Size(NumberField.BUTTON_SIZE, VALUE_HEIGHT))
				.build();
		
		private static final StyleRule NUMBER_FIELD_STYLE = when().type("number_field").then()
				.set(SPACING, 2)
				.set(DIR, Axis.HORIZONTAL)
				.build();
		private static final StyleRule NUMBER_FIELD_MINUS_STYLE = when().hasClass("number_field:minus_button").then()
				.set(SIZE_OVERRIDE, new Size(NumberField.BUTTON_SIZE, NumberField.BUTTON_SIZE))
				.build();
		private static final StyleRule NUMBER_FIELD_PLUS_STYLE = when().hasClass("number_field:plus_button").then()
				.set(SIZE_OVERRIDE, new Size(NumberField.BUTTON_SIZE, NumberField.BUTTON_SIZE))
				.build();

		private static final StyleRule FILTERED_OUT_STYLE = when().is(IBetterElement.FILTERED_OUT).then()
				.set(VISIBILITY, Visibility.COLLAPSED)
				.build();

		private static final StyleRule FOLDOUT_STYLE = when().type("better:foldout").then()
				.set(DIR, Axis.VERTICAL)
				.build();
		private static final StyleRule FOLDED_STYLE = when().parent().is(Foldout.FOLDED).then()
				.set(VISIBILITY, Visibility.COLLAPSED)
				.build();
		private static final StyleRule FOLDOUT_HEADER_STYLE = when().type("better:foldout_header").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, Foldout.FOLDOUT_HEADER_HEIGHT))
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();

		private static final StyleRule ROOT_GROUP_STYLE = when().hasClass("better:root_group").then()
				.set(OUTER_PADDING, new Padding(6, 6 + 6, 6, 6))
				.build();
		private static final StyleRule TABLE_STYLE = when().hasClass("better:table_group").then()
				.set(OUTER_PADDING, new Padding(0, 0, 0, SECTION_TAB_SIZE))
				.build();
		private static final StyleRule LIST_STYLE = when().hasClass("better:list_group").then()
				.set(OUTER_PADDING, new Padding(0, 0, 0, SECTION_TAB_SIZE))
				.build();

		private static final StyleRule LIST_ENTRY_STYLE = when().type("better:list_entry").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(OUTER_PADDING, new Padding(0, 0, 0, -VALUE_HEIGHT))
				.build();
		private static final StyleRule LIST_ENTRY_REMOVE_STYLE = when().hasClass("better:list_remove").then()
				.set(VISIBILITY, Visibility.HIDDEN)
				.set(OUTER_PADDING, new Padding((VALUE_CONTAINER_HEIGHT - VALUE_HEIGHT) / 2, 0, 0, 0))
				.build();
		private static final StyleRule LIST_ENTRY_REMOVE_HOVERED_STYLE = when().hasClass("better:list_remove").parent().is(Component.HOVERED).then()
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();
		private static final StyleRule LIST_ENTRY_REMOVE_FOCUSED_STYLE = when().hasClass("better:list_remove").parent().is(Component.FOCUSED).then()
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();

		private static final StyleRule ROOT_STYLE = when().type("better:root").then()
				.set(DIR, Axis.VERTICAL)
				.set(SPACING, Y_PADDING)
				.set(INNER_PADDING, new Padding(Y_PADDING, X_PADDING, Y_PADDING, X_PADDING))
				.build();
		private static final StyleRule SEARCH_BAR_STYLE = when().hasClass("better:search_field").then()
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, 18))
				.build();
		private static final StyleRule SEARCH_LABEL_STYLE = when().hasClass("better:search_label").then()
				.set(SIZE_OVERRIDE, new Size(SEARCH_LABEL_WIDTH, 18))
				.build();
		private static final StyleRule TAB_BAR_STYLE = when().hasClass("better:tab_bar").then()
				.set(DIR, Axis.HORIZONTAL)
				.build();
		private static final StyleRule BOTTOM_BAR_STYLE = when().hasClass("better:bottom_bar").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(SPACING, X_PADDING)
				.build();

		private static final StyleRule VALUE_ENTRY_STYLE = when().type("better:value_entry").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(JUSTIFICATION, Alignment.CENTER)
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, VALUE_CONTAINER_HEIGHT))
				.build();

		private static final StyleRule ENTRY_UNDO_STYLE = when().hasClass("better:undo").then()
				.set(VISIBILITY, Visibility.HIDDEN)
				.build();

		private static final StyleRule ENTRY_UNDO_HOVERED_STYLE = when().hasClass("better:undo").parent().is(Component.HOVERED).then()
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();
		private static final StyleRule ENTRY_UNDO_FOCUSED_STYLE = when().hasClass("better:undo").parent().is(Component.FOCUSED).then()
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();
		private static final StyleRule ENTRY_UNDO_LIST_HOVERED_STYLE = when()
			.hasClass("better:undo")
			.parent()
				.parent()
					.type("better:list_entry")
			.parent()
				.parent()
					.is(Component.HOVERED)
			.then()
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();
		private static final StyleRule ENTRY_UNDO_LIST_FOCUSED_STYLE = when()
			.hasClass("better:undo")
			.parent()
				.parent()
					.type("better:list_entry")
			.parent()
				.parent()
					.is(Component.FOCUSED)
			.then()
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();

		private static final StyleRule OPTION_BUTTON_STYLE = when().hasClass("better:option_button").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule STRING_INPUT_FIELD_STYLE = when().hasClass("better:string_input").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule UNKNOWN_OPTION_STYLE = when().hasClass("better:unknown").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule HBOX_STYLE = when().type("hbox").then()
				.set(DIR, Axis.HORIZONTAL)
				.build();
		
		private static final StyleRule BETTER_BUTTON_STYLE = when().hasClass("better:button").then()
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, Constants.VALUE_HEIGHT))
				.build();
		private static final StyleRule BETTER_ICON_BUTTON_STYLE = when().hasClass("better:icon_button").then()
				.set(SIZE_OVERRIDE, new Size(Constants.VALUE_HEIGHT, Constants.VALUE_HEIGHT))
				.build();

		public static StyleSheet.Builder builder()
		{
			return new StyleSheet.Builder(
					FILTERED_OUT_STYLE, ROOT_STYLE, SEARCH_BAR_STYLE, SEARCH_LABEL_STYLE, TAB_BAR_STYLE, BOTTOM_BAR_STYLE, FOLDOUT_STYLE, FOLDOUT_HEADER_STYLE, FOLDED_STYLE,
					LIST_ENTRY_STYLE, LIST_ENTRY_REMOVE_HOVERED_STYLE, LIST_ENTRY_REMOVE_FOCUSED_STYLE, LIST_ENTRY_REMOVE_STYLE,
					VALUE_ENTRY_STYLE, ENTRY_UNDO_HOVERED_STYLE, ENTRY_UNDO_FOCUSED_STYLE, ENTRY_UNDO_LIST_HOVERED_STYLE, ENTRY_UNDO_LIST_FOCUSED_STYLE, ENTRY_UNDO_STYLE,
					OPTION_BUTTON_STYLE, STRING_INPUT_FIELD_STYLE, UNKNOWN_OPTION_STYLE, ROOT_GROUP_STYLE, TABLE_STYLE, LIST_STYLE,
					BETTER_NUMBER_FIELD_STYLE, BETTER_NUMBER_FIELD_PLUS_STYLE, BETTER_NUMBER_FIELD_MINUS_STYLE, NUMBER_FIELD_STYLE, NUMBER_FIELD_PLUS_STYLE, NUMBER_FIELD_MINUS_STYLE,
					BETTER_ICON_BUTTON_STYLE, BETTER_BUTTON_STYLE, HBOX_STYLE);
		}
	}
}

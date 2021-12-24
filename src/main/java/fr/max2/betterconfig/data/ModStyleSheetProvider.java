package fr.max2.betterconfig.data;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.client.gui.better.BetterConfigBuilder;
import fr.max2.betterconfig.client.gui.better.Foldout;
import fr.max2.betterconfig.client.gui.better.GuiRoot;
import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.better.ListElementEntry;
import fr.max2.betterconfig.client.gui.better.ValueEntry;
import fr.max2.betterconfig.client.gui.better.widget.OptionButton;
import fr.max2.betterconfig.client.gui.better.widget.StringInputField;
import fr.max2.betterconfig.client.gui.better.widget.UnknownOptionWidget;
import fr.max2.betterconfig.client.gui.component.HBox;
import fr.max2.betterconfig.client.gui.component.widget.NumberField;
import fr.max2.betterconfig.client.gui.component.widget.TextField;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import fr.max2.betterconfig.client.gui.style.StyleRule.Serializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ModStyleSheetProvider implements DataProvider
{
	public static Gson GSON = new GsonBuilder().registerTypeAdapter(StyleRule.class, Serializer.INSTANCE).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator generator;
	
	public ModStyleSheetProvider(DataGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public void run(HashCache pCache) throws IOException
	{
		StyleSheet.Builder defaultStyleSheet = new StyleSheet.Builder(
				IBetterElement.STYLE, GuiRoot.ROOT_STYLE, GuiRoot.SEARCH_STYLE, Foldout.STYLE, ListElementEntry.STYLE, ListElementEntry.REMOVE_STYLE, ValueEntry.STYLE,
				OptionButton.STYLE, StringInputField.STYLE, UnknownOptionWidget.STYLE, BetterConfigBuilder.ROOT_STYLE, BetterConfigBuilder.TABLE_STYLE, BetterConfigBuilder.LIST_STYLE,
				HBox.STYLE, TextField.STYLE, NumberField.FIELD_STYLE, NumberField.PLUS_STYLE, NumberField.MINUS_STYLE);
		
		extracted(pCache, defaultStyleSheet, StyleSheet.DEFAULT_STYLESHEET);
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

}

package fr.max2.betterconfig.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.widget.CycleOptionButton;
import fr.max2.betterconfig.client.util.GuiTexts;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguagesProvider implements DataProvider
{
	/** The list of supported languages */
	private final List<PartialLanguageProvider> languages = new ArrayList<>();

	protected ModLanguagesProvider(PackOutput output, String modId, String... locales)
	{
		for (String locale : locales)
		{
			this.languages.add(new PartialLanguageProvider(output, modId, locale));
		}
	}

	public ModLanguagesProvider(PackOutput output)
	{
		this(output, BetterConfig.MODID, "en_us", "fr_fr");
	}

	protected void addTranslations()
    {
		this.add(CycleOptionButton.NO_OPTION_KEY, "NONE", "AUCUN");
		this.add(CycleOptionButton.TRUE_OPTION_KEY, "ON", "Oui");
		this.add(CycleOptionButton.FALSE_OPTION_KEY, "OFF", "Non");
		this.add(GuiTexts.CANCEL_CONFIG_KEY, "Cancel changes", "Annuler les modifications");

		this.add(GuiTexts.DEFAULT_VALUE_KEY, "Default: %s", "Par défaut : %s");
		this.add(GuiTexts.SEARCH_BAR_KEY, "Search", "Rechercher");
		this.add(GuiTexts.LIST_ELEMENT_LABEL_KEY, "%s[%d]", "%s[%d]");
		this.add(GuiTexts.ADD_ELEMENT_KEY, "Add", "Ajouter");
		this.add(GuiTexts.ADD_FIRST_TOOLTIP_KEY, "Add a new element at the start of the list", "Ajouter un nouvel élément au début de la liste");
		this.add(GuiTexts.ADD_LAST_TOOLTIP_KEY, "Add a new element at the end of the list", "Ajouter un nouvel élément à la fin de la liste");
		this.add(GuiTexts.REMOVE_TOOLTIP_KEY, "Remove this element from the list", "Retirer cet élément de la liste");
		this.add(GuiTexts.UNDO_TOOLTIP_KEY, "Undo", "Annuler le changement");
		this.add(GuiTexts.RESET_TOOLTIP_KEY, "Reset to default", "Réinitialiserà la valeur par défaut");

		// Narration
		this.add(GuiTexts.SECTION_TITLE_SHOWN, "%s section", "Section %s");
		this.add(GuiTexts.SECTION_TITLE_COLLAPSED, "%s collapsed section", "Section repliée %s");
		this.add(GuiTexts.SECTION_USAGE_FOCUSED, "Press Enter to show or hide", "Appuyez sur Entrée pour afficher ou cacher");
		this.add(GuiTexts.SECTION_USAGE_HOVERED, "Left click to show or hide", "Faites un clic gauche pour afficher ou cacher");
    }

	@Override
	public CompletableFuture<?> run(CachedOutput pOutput)
	{
		this.addTranslations();
		List<CompletableFuture<?>> futures = new ArrayList<>();
		for (LanguageProvider language : this.languages)
		{
			futures.add(language.run(pOutput));
		}
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	/** Add the translations of the given key for all languages */
	protected void add(String key, String... values)
	{
		Preconditions.checkArgument(this.languages.size() == values.length, "The number of provided values should be equal to the number of languages");
		for (int i = 0; i < this.languages.size(); i++)
		{
			this.languages.get(i).add(key, values[i]);
		}
	}

	@Override
	public String getName()
	{
		return BetterConfig.MODID + " Languages";
	}

	private static class PartialLanguageProvider extends LanguageProvider
	{
		public PartialLanguageProvider(PackOutput output, String modid, String locale)
		{
			super(output, modid, locale);
		}

		@Override
		protected void addTranslations()
		{ }
	}
}

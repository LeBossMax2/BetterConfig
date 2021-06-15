package fr.max2.betterconfig.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.builder.BetterConfigBuilder;
import fr.max2.betterconfig.client.gui.component.CycleOptionButton;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguagesProvider implements IDataProvider 
{
	/** The list of supported languages */
	private final List<PartialLanguageProvider> languages = new ArrayList<>();
	
	protected ModLanguagesProvider(DataGenerator gen, String modId, String... locales)
	{
		for (String locale : locales)
		{
			this.languages.add(new PartialLanguageProvider(gen, modId, locale));
		}
	}
	
	public ModLanguagesProvider(DataGenerator gen)
	{
		this(gen, BetterConfig.MODID, "en_us", "fr_fr");
	}
	
	protected void addTranslations()
    {
		add(CycleOptionButton.NO_OPTION_KEY, "NONE", "AUCUN");
		add(CycleOptionButton.TRUE_OPTION_KEY, "ON", "Oui");
		add(CycleOptionButton.FALSE_OPTION_KEY, "OFF", "Non");
		add(BetterConfigBuilder.DEFAULT_VALUE_KEY, "Default: %s", "Par défaut : %s");
		add(BetterConfigBuilder.SEARCH_BAR_KEY, "Search", "Rechercher");
    }
	
	@Override
	public void act(DirectoryCache cache) throws IOException
	{
		this.addTranslations();
		for (LanguageProvider language : this.languages)
		{
			language.act(cache);
		}
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
		public PartialLanguageProvider(DataGenerator gen, String modid, String locale)
		{
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations()
		{ }
	}
}

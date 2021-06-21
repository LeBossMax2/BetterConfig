package fr.max2.betterconfig.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.max2.betterconfig.config.spec.ConfigTableEntrySpec;

public class ConfigFilter
{
	public static final ConfigFilter ALL = new ConfigFilter(Collections.emptyList());
	
	private final List<String> words;
	
	private ConfigFilter(List<String> words)
	{
		this.words = words;
	}
	
	public ConfigFilter()
	{
		this(new ArrayList<>());
	}

	public void setFilter(String filter)
	{
		this.words.clear();
		if (filter == null)
			return;
		
		for (String word : filter.split("[ .,]"))
		{
			if (!word.isEmpty())
			{
				this.words.add(word.toLowerCase());
			}
		}
	}
	
	public boolean matches(ConfigTableEntrySpec entry)
	{
		for (String word : this.words)
		{
			if (!entryMatchesWord(entry, word))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean entryMatchesWord(ConfigTableEntrySpec entry, String word)
	{
		String comment = entry.getCommentString(); 
		return comment != null && comment.toLowerCase().contains(word)
		    || entry.getDisplayName().getString().toLowerCase().contains(word)
		    || entry.getLoc().getPath().stream().anyMatch(p -> p.toLowerCase().contains(word));
	}
}

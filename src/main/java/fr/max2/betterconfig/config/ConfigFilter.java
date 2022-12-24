package fr.max2.betterconfig.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigFilter
{
	public static final ConfigFilter ALL = new ConfigFilter(Collections.emptyList());
	public static final ConfigFilter NONE = new ConfigFilter(Collections.emptyList())
	{
		@Override
		public boolean matches(IConfigName node)
		{
			return false;
		}
	};
	
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
		
		for (String word : filter.split("[ .,/]"))
		{
			if (!word.isEmpty())
			{
				this.words.add(word.toLowerCase());
			}
		}
	}
	
	public boolean matches(IConfigName node)
	{
		for (String word : this.words)
		{
			if (!entryMatchesWord(node, word))
				return false;
		}
		return true;
	}
	
	private static boolean entryMatchesWord(IConfigName node, String word)
	{
		String comment = node.getCommentString(); 
		return comment != null && comment.toLowerCase().contains(word)
		    || node.getDisplayName().getString().toLowerCase().contains(word)
		    || node.getPath().stream().anyMatch(p -> p.toLowerCase().contains(word));
	}
}

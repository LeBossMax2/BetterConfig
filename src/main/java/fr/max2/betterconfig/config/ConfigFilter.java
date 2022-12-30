package fr.max2.betterconfig.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.max2.betterconfig.client.gui.better.ConfigName;

public class ConfigFilter
{
	public static final ConfigFilter ALL = new ConfigFilter(Collections.emptyList());
	public static final ConfigFilter NONE = new ConfigFilter(Collections.emptyList())
	{
		@Override
		public boolean matches(ConfigName node)
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
	
	public boolean matches(ConfigName node)
	{
		for (String word : this.words)
		{
			if (!entryMatchesWord(node, word))
				return false;
		}
		return true;
	}
	
	private static boolean entryMatchesWord(ConfigName node, String word)
	{
		String comment = node.getCommentString();
		return comment != null && comment.toLowerCase().contains(word)
		    || node.getDisplayName().getString().toLowerCase().contains(word)
		    || node.getPath().stream().anyMatch(p -> p.toLowerCase().contains(word));
	}
}

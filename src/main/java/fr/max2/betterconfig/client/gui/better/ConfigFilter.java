package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

		for (String word : filter.split("[ .,/]"))
		{
			if (!word.isEmpty())
			{
				this.words.add(word.toLowerCase());
			}
		}
	}

	public boolean matches()
	{
		return this.words.isEmpty();
	}

	public ConfigFilter apply(ConfigName node)
	{
		int i = this.words.size() - 1;
		boolean noMatch = true;
		for (; i >= 0; i--)
		{
			if (entryMatchesWord(node, this.words.get(i)))
			{
				noMatch = false;
				break;
			}
		}

		if (noMatch)
		{
			return this;
		}

		var wordsLeft = new ArrayList<>(this.words);
		for (; i >= 0; i--)
		{
			if (entryMatchesWord(node, this.words.get(i)))
			{
				wordsLeft.remove(i);
			}
		}

		return wordsLeft.isEmpty() ? ALL : new ConfigFilter(wordsLeft);
	}

	private static boolean entryMatchesWord(ConfigName node, String word)
	{
		String comment = node.getCommentString();
		return comment != null && comment.toLowerCase().contains(word)
		    || node.getDisplayName().getString().toLowerCase().contains(word)
		    || node.getName().toLowerCase().contains(word);
	}
}

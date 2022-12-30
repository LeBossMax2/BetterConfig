package fr.max2.betterconfig.config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import net.minecraft.network.chat.Component;

public class ConfigTableKey
{
	private final String name;
	private final Component displayName;
	private final String commentString;
	/** The comments describing the property */
	private List<? extends Component> commentLines = null;

	public ConfigTableKey(String name, Component displayName, String comment)
	{
		this.name = name;
		this.displayName = displayName;
		this.commentString = comment;
	}

	public String getName()
	{
		return this.name;
	}

	/**
	 * Gets the display name of the property
	 */
	public Component getDisplayName()
	{
		return this.displayName;
	}

	/**
	 * Gets the comment associated with the config node
	 */
	public String getCommentString()
	{
		return this.commentString;
	}

	/**
	 * Gets the comment associated with the config node for display
	 * @return an unmodifiable list containing the comments
	 */
	public List<? extends Component> getDisplayComment()
	{
		if (this.commentLines == null)
		{
			String comment = this.getCommentString();
			if (Strings.isNullOrEmpty(comment))
			{
				this.commentLines = Collections.emptyList();
			}
			else
			{
				this.commentLines = Stream.of(comment.split("\n")).map(Component::literal).collect(Collectors.toList());
			}
		}
		return this.commentLines;
	}
}

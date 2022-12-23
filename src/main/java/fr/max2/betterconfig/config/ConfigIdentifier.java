package fr.max2.betterconfig.config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import fr.max2.betterconfig.config.spec.ConfigLocation;
import net.minecraft.network.chat.Component;

public class ConfigIdentifier implements IConfigName
{
	private final ConfigLocation loc;
	private final Component displayName;
	private final String commentString;
	/** The comments describing the property */
	private List<? extends Component> commentLines = null;
	
	public ConfigIdentifier(ConfigLocation loc, Component displayName, String comment)
	{
		this.loc = loc;
		this.displayName = displayName;
		this.commentString = comment;
	}

	public ConfigLocation getLoc()
	{
		return this.loc;
	}

	@Override
	public String getName()
	{
		return this.loc.getName();
	}

	@Override
	public List<String> getPath()
	{
		return this.loc.getPath();
	}

	/**
	 * Gets the display name of the property
	 */
	@Override
	public Component getDisplayName()
	{
		return this.displayName;
	}
	
	/**
	 * Gets the comment associated with the config node
	 */
	@Override
	public String getCommentString()
	{
		return this.commentString;
	}

	/**
	 * Gets the comment associated with the config node for display
	 * @return an unmodifiable list containing the comments
	 */
	@Override
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

package fr.max2.betterconfig.config.spec;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ConfigTableEntrySpec
{
	private final ConfigLocation loc;
	private final IConfigSpecNode node;
	private final ITextComponent displayName;
	private final String commentString;
	/** The comments describing the property */
	private List<? extends ITextComponent> commentLines = null;
	
	public ConfigTableEntrySpec(ConfigLocation loc, IConfigSpecNode node, ITextComponent displayName, String comment)
	{
		this.loc = loc;
		this.node = node;
		this.displayName = displayName;
		this.commentString = comment;
	}
	
	public ConfigTableEntrySpec(ConfigLocation loc, IConfigSpecNode node, String comment)
	{
		this(loc, node, new StringTextComponent(loc.getName()), comment);
	}

	public ConfigLocation getLoc()
	{
		return loc;
	}
	
	public IConfigSpecNode getNode()
	{
		return node;
	}

	/**
	 * Gets the display name of the property
	 */
	public ITextComponent getDisplayName()
	{
		return displayName;
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
	public List<? extends ITextComponent> getDisplayComment()
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
				this.commentLines = Stream.of(comment.split("\n")).map(StringTextComponent::new).collect(Collectors.toList());
			}
		}
		return this.commentLines;
	}
}

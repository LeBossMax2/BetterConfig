package fr.max2.betterconfig.config.spec;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public abstract class ConfigSpecNode
{
	private final ConfigLocation loc;
	/** The comments describing the property */
	private List<? extends ITextComponent> commentLines = null;
	
	public ConfigSpecNode(ConfigLocation loc)
	{
		this.loc = loc;
	}

	public ConfigLocation getLoc()
	{
		return loc;
	}
	
	/**
	 * Gets the comment associated with the config node
	 */
	public abstract String getCommentString();
	
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
	
	public <R> R exploreNode(IConfigSpecVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	public abstract <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param);
}

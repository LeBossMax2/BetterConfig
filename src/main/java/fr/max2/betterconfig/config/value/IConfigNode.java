package fr.max2.betterconfig.config.value;

import java.util.List;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import net.minecraft.util.text.ITextComponent;

public interface IConfigNode<T>
{
	IConfigSpecNode<T> getSpec();
	
	//IConfigNode<?> getParent();
	
	String getName();
	
	ITextComponent getDisplayName();

	List<String> getPath();
	
	String getCommentString();
	
	List<? extends ITextComponent> getDisplayComment();
	
	default <R> R exploreNode(IConfigValueVisitor<Void, R> visitor)
	{
		return this.exploreNode(visitor, null);
	}
	
	<P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param);
}

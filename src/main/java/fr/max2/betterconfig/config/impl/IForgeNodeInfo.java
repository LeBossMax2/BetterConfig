package fr.max2.betterconfig.config.impl;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.util.text.ITextComponent;

public interface IForgeNodeInfo
{
	String getName();
	
	ITextComponent getDisplayName();

	Stream<String> getPath();
	
	String getCommentString();
	
	List<? extends ITextComponent> getDisplayComment();
}

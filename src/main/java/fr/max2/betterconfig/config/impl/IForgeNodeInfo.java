package fr.max2.betterconfig.config.impl;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.network.chat.Component;

public interface IForgeNodeInfo
{
	String getName();
	
	Component getDisplayName();

	Stream<String> getPath();
	
	String getCommentString();
	
	List<? extends Component> getDisplayComment();
}

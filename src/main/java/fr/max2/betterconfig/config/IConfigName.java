package fr.max2.betterconfig.config;

import java.util.List;

import net.minecraft.network.chat.Component;

public interface IConfigName
{
	String getName();
	
	Component getDisplayName();

	List<String> getPath();
	
	String getCommentString();
	
	List<? extends Component> getDisplayComment();
}

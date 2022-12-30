package fr.max2.betterconfig.client.gui.better;

import java.util.List;

import net.minecraft.network.chat.Component;

public interface ConfigName
{
	String getName();
	
	Component getDisplayName();

	List<String> getPath();
	
	String getCommentString();
	
	List<? extends Component> getDisplayComment();
}

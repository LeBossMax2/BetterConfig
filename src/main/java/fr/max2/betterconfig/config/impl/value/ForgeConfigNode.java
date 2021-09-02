package fr.max2.betterconfig.config.impl.value;

import java.util.List;
import java.util.stream.Collectors;

import fr.max2.betterconfig.config.impl.IForgeNodeInfo;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.value.IConfigNode;
import net.minecraft.network.chat.Component;

public abstract class ForgeConfigNode<T, Spec extends IConfigSpecNode<T>, Info extends IForgeNodeInfo> implements IConfigNode<T>
{
	private final Spec spec;
	protected final Info info;
	
	public ForgeConfigNode(Spec spec, Info info)
	{
		this.spec = spec;
		this.info = info;
	}
	
	@Override
	public Spec getSpec()
	{
		return this.spec;
	}
	
	@Override
	public String getName()
	{
		return this.info.getName();
	}
	
	@Override
	public Component getDisplayName()
	{
		return this.info.getDisplayName();
	}
	
	@Override
	public List<String> getPath()
	{
		return this.info.getPath().collect(Collectors.toList());
	}
	
	@Override
	public String getCommentString()
	{
		return this.info.getCommentString();
	}
	
	@Override
	public List<? extends Component> getDisplayComment()
	{
		return this.info.getDisplayComment();
	}
	
	protected abstract T getCurrentValue();
}

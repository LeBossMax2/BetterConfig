package fr.max2.betterconfig.config.impl.value;

import java.util.function.Consumer;

import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.value.IConfigNode;

public abstract class ForgeConfigNode<T, Spec extends IConfigSpecNode<T>> implements IConfigNode<T>
{
	private final Spec spec;
	/** The function to call then the value is changed */
	protected final Consumer<ForgeConfigProperty<?, ?>> changeListener;
	
	public ForgeConfigNode(Spec spec, Consumer<ForgeConfigProperty<?, ?>> changeListener)
	{
		this.spec = spec;
		this.changeListener = changeListener;
	}
	
	@Override
	public Spec getSpec()
	{
		return this.spec;
	}
}

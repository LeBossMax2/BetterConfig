package fr.max2.betterconfig.config.spec;

import java.util.List;

import com.google.common.base.Strings;

import fr.max2.betterconfig.config.ValueType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ConfigPropertySpec<T> extends ConfigSpecNode
{
	/** The specification */
	private final ValueSpec spec;
	/** The type of the property */
	private final ValueType type;
	
	public ConfigPropertySpec(ConfigLocation loc, ValueSpec spec)
	{
		super(loc);
		this.spec = spec;
		this.type = ValueType.getType(this.getValueClass());
	}
	
	/**
	 * Gets the display name of the property
	 */
	public ITextComponent getDisplayName()
	{
		// Try getting name from translation key 
		String translationKey = this.spec.getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey))
			return new TranslationTextComponent(translationKey);
		
		// Try getting name from path
		List<String> path = this.getLoc().getPath();
		if (!path.isEmpty())
			return new StringTextComponent(path.get(path.size() - 1));
		
		// No name found :(
		return StringTextComponent.EMPTY;
	}
	
	/**
	 * Checks if the given value is a valid value
	 * @param value the value to check
	 * @return true if the value matches the spec, false otherwise
	 */
	public boolean isAllowed(T value)
	{
		return this.spec.test(value);
	}
	
	/**
	 * Correct the given value to match the spec
	 * @param value the value to fix
	 * @return a valid value
	 */
	@SuppressWarnings("unchecked")
	public T correct(T value)
	{
		return (T)this.spec.correct(value);
	}
	
	/**
	 * Gets the default configuration value
	 */
	@SuppressWarnings("unchecked")
	public T getDefaultValue()
	{
		return (T)this.spec.getDefault();
	}
	
	public ValueType getType()
	{
		return this.type;
	}
	
	@Override
	public String getCommentString()
	{
		return this.spec.getComment();
	}
	
	/**
	 * Gets the class of the configuration value
	 */
	public Class<?> getValueClass()
	{
		Class<?> specClass = this.spec.getClazz();
		if (specClass != Object.class)
			return specClass;
		
		T value = this.getDefaultValue();
		if (value != null)
			return value.getClass();
		
		return Object.class;
	}
	
	@Override
	public <P, R> R exploreNode(IConfigSpecVisitor<P, R> visitor, P param)
	{
		return visitor.visitProperty(this, param);
	}
}

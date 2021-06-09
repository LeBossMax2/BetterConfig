package fr.max2.betterconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Strings;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public class ConfigProperty<T>
{
	private final ValueSpec spec;
	private final ConfigValue<T> configValue;
	private List<String> commentLines = null;
	
	public ConfigProperty(ValueSpec spec, ConfigValue<T> configValue)
	{
		this.spec = spec;
		this.configValue = configValue;
	}
	
	public ITextComponent getName()
	{
		// Try getting name from translation key 
		String translationKey = this.spec.getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey))
			return new TranslationTextComponent(translationKey);
		
		// Try getting name from path
		List<String> path = this.configValue.getPath();
		if (!path.isEmpty())
			return new StringTextComponent(path.get(path.size() - 1));
		
		// No name found :(
		return StringTextComponent.EMPTY;
	}
	
	public List<String> getPath()
	{
		return this.configValue.getPath();
	}
	
	public boolean isAllowed(T value)
	{
		return this.spec.test(value);
	}
	
	@SuppressWarnings("unchecked")
	public T correct(T value)
	{
		return (T)this.spec.correct(value);
	}
	
	public T getValue()
	{
		return this.configValue.get();
	}
	
	public void setValue(T value)
	{
		if (!Objects.equals(this.getValue(), value))
			this.configValue.set(value);
	}
	
	@SuppressWarnings("unchecked")
	public T getDefaultValue()
	{
		return (T)this.spec.getDefault();
	}
	
	public List<String> getComments()
	{
		if (this.commentLines == null)
		{
			String comment = this.spec.getComment();
			if (Strings.isNullOrEmpty(comment))
			{
				this.commentLines = Collections.emptyList();
			}
			else
			{
				this.commentLines = Arrays.asList(comment.split("\n"));
			}
		}
		return this.commentLines;
	}
	
	public Class<?> getValueClass()
	{
		Class<?> specClass = spec.getClazz();
		if (specClass != Object.class)
			return specClass;
		
		T value = this.configValue.get();
		if (value != null)
			return value.getClass();
		
		return Object.class;
	}
	
	@SuppressWarnings("unchecked")
	public <R> Optional<ConfigProperty<? extends R>> tryCast(Class<R> clazz)
	{
		if (clazz.isAssignableFrom(this.getValueClass()))
		{
			return Optional.of((ConfigProperty<? extends R>)this);
		}
		return Optional.empty();
	}
	
}

package fr.max2.betterconfig.util;

import java.util.Optional;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public interface INumberType<N>
{
	N parse(String value) throws NumberFormatException;
	
	default Optional<N> tryParse(String value)
	{
		try
		{
			return Optional.of(this.parse(value));
		}
		catch (NumberFormatException e)
		{
			return Optional.empty();
		}
	}
	
	default String intoString(N value)
	{
		return value.toString();
	}
	
	N applyOperation(N value, Operator op, Increment inc);
	
	public static enum Increment
	{
		LOW("-", "+"),
		NORMAL("-", "+", TextFormatting.BOLD),
		HIGH("--", "++", TextFormatting.BOLD);
		
		private final ITextComponent minusText;
		private final ITextComponent plusText;
		
		private Increment(String minusText, String plusText, TextFormatting... formats)
		{
			this.minusText = new StringTextComponent(minusText).mergeStyle(formats);
			this.plusText = new StringTextComponent(plusText).mergeStyle(formats);
		}
		
		public ITextComponent getMinusText()
		{
			return minusText;
		}
		
		public ITextComponent getPlusText()
		{
			return plusText;
		}
	}
	
	public static enum Operator
	{
		PLUS(1),
		MINUS(-1);
		
		private final int multiplier;

		private Operator(int multiplier)
		{
			this.multiplier = multiplier;
		}
		
		public int getMultiplier()
		{
			return multiplier;
		}
	}
}

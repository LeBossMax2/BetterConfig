package fr.max2.betterconfig.client.util;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * Represents a type of numbers
 */
public interface INumberType<N>
{
	/**
	 * Parses the given string into a number
	 * @param value the string to parse
	 * @return the parsed number
	 * @throws NumberFormatException if the format is not respected
	 */
	N parse(String value) throws NumberFormatException;
	
	/**
	 * Tries to parse the given string into a number
	 * @param value the string to parse
	 * @return The parsed number or an empty optional is the format is not respected
	 */
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
	
	/**
	 * Converts the number into a string
	 * @param value the number to convert
	 * @return a string representation of the number
	 */
	default String intoString(N value)
	{
		return Objects.toString(value);
	}
	
	/**
	 * Applies the operation to the number
	 * @param value the number to apply the operation to
	 * @param op the operator of the operation
	 * @param inc the operand
	 * @return the result of the operation
	 */
	N applyOperation(N value, Operator op, Increment inc);
	
	/**
	 * Represent a value increment
	 */
	public static enum Increment
	{
		/** A small increment, smaller than 1 */
		LOW("-", "+"),
		/** A regular increment, about 1 */
		NORMAL("-", "+", TextFormatting.BOLD),
		/** A bug increment, bigger than 1 */
		HIGH("--", "++", TextFormatting.BOLD);
		
		/** The text to show on a plus button with this increment */
		private final ITextComponent minusText;
		/** The text to show on a minus button with this increment */
		private final ITextComponent plusText;
		
		private Increment(String minusText, String plusText, TextFormatting... formats)
		{
			this.minusText = new StringTextComponent(minusText).mergeStyle(formats);
			this.plusText = new StringTextComponent(plusText).mergeStyle(formats);
		}

		/** The text to show on a plus button with this increment */
		public ITextComponent getMinusText()
		{
			return minusText;
		}

		/** The text to show on a minus button with this increment */
		public ITextComponent getPlusText()
		{
			return plusText;
		}
	}
	
	/**
	 * Represents an operator
	 */
	public static enum Operator
	{
		/** Addition */
		PLUS(1),
		/** Subtraction */
		MINUS(-1);
		
		/** The multiplier to apply before an addition */
		private final int multiplier;

		private Operator(int multiplier)
		{
			this.multiplier = multiplier;
		}
		
		/** Gets the multiplier to apply before an addition */
		public int getMultiplier()
		{
			return multiplier;
		}
	}
}

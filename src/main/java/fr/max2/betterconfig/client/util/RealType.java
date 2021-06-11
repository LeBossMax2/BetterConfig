package fr.max2.betterconfig.client.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Function;

public final class RealType<N extends Number> implements INumberType<N>
{
	public static final RealType<Float> FLOAT =new RealType<>(Float::parseFloat, "#0.0####", Number::floatValue);
	public static final RealType<Double> DOUBLE = new RealType<>(Double::parseDouble, "#0.0#########", Number::doubleValue);

	/** The function to parse the number */
	private final Function<String, N> parser;
	/** The format to use to convert the number into string */
	private final DecimalFormat formater;
	/** The function to convert a number into the represented integer */
	private final Function<Number, N> converter;

	private RealType(Function<String, N> parser, String format, Function<Number, N> converter)
	{
		this.parser = parser;
		this.formater = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		this.converter = converter;
	}

	@Override
	public N parse(String value) throws NumberFormatException
	{
		return this.parser.apply(value);
	}
	
	@Override
	public String intoString(N value)
	{
		return this.formater.format(value.doubleValue());
	}

	@Override
	public N applyOperation(N value, Operator op, Increment inc)
	{
		double left;
		switch (inc)
		{
		case HIGH:
			left = 10.0;
			break;
		case LOW:
			left = 0.1;
			break;
		default:
		case NORMAL:
			left = 1.0;
			break;
		}
		
		return this.converter.apply(value.doubleValue() + op.getMultiplier() * left);
	}
	
}

package fr.max2.betterconfig.client.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Function;

public final class RealType<N extends Number> implements INumberType<N>
{
	public static final RealType<Float> FLOAT = new RealType<>(Float::parseFloat, Number::floatValue, "#0.0####");
	public static final RealType<Double> DOUBLE = new RealType<>(Double::parseDouble, Number::doubleValue, "#0.0#########");

	/** The function to parse the number */
	private final Function<String, N> parser;
	/** The function to convert a number into the represented integer */
	private final Function<Number, N> converter;
	/** The format to use to convert the number into string */
	private final DecimalFormat formater;

	private RealType(Function<String, N> parser, Function<Number, N> converter, String format)
	{
		this.parser = parser;
		this.converter = converter;
		this.formater = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	}

	@Override
	public N parse(String value) throws NumberFormatException
	{
		return this.parser.apply(value);
	}

	@Override
	public String intoString(N value)
	{
		return value == null ? "null" : this.formater.format(value.doubleValue());
	}

	@Override
	public N applyOperation(N value, Operator op, Increment inc)
	{
		double left = switch (inc)
		{
			case HIGH	-> 10.0;
			case NORMAL	-> 1.0;
			case LOW	-> 0.1;
		};

		return this.converter.apply(value.doubleValue() + op.getMultiplier() * left);
	}

}

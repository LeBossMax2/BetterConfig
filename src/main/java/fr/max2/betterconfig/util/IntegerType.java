package fr.max2.betterconfig.util;

import java.util.function.Function;

public final class IntegerType<N extends Number> implements INumberType<N>
{
	public static final IntegerType<Byte> BYTE = new IntegerType<>(Byte::parseByte, Number::byteValue);
	public static final IntegerType<Short> SHORT = new IntegerType<>(Short::parseShort, Number::shortValue);
	public static final IntegerType<Integer> INTERGER = new IntegerType<>(Integer::parseInt, Number::intValue);
	public static final IntegerType<Long> LONG = new IntegerType<>(Long::parseLong, Number::longValue);
	
	private final Function<String, N> parser;
	private final Function<Number, N> converter;

	private IntegerType(Function<String, N> parser, Function<Number, N> converter)
	{
		this.parser = parser;
		this.converter = converter;
	}

	@Override
	public N parse(String value) throws NumberFormatException
	{
		return this.parser.apply(value);
	}

	@Override
	public N applyOperation(N value, Operator op, Increment inc)
	{
		long left;
		switch (inc)
		{
		case HIGH:
			left = 10;
			break;
		case LOW:
			left = 1;
			break;
		default:
		case NORMAL:
			left = 1;
			break;
		}
		
		return this.converter.apply(value.longValue() + op.getMultiplier() * left);
	}
	
}

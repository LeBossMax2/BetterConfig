package fr.max2.betterconfig.client.gui.layout;


public class Size
{
	public static int UNCONSTRAINED = -1;
	
	public int width;
	public int height;
	
	public Size()
	{
		this(UNCONSTRAINED, UNCONSTRAINED);
	}
	
	public Size(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public int get(Axis axis)
	{
		switch (axis)
		{
		case HORIZONTAL:
			return this.width;
		case VERTICAL:
			return this.height;
		default:
			return -1;
		}
	}
	
	public void set(Axis axis, int size)
	{
		switch (axis)
		{
		case HORIZONTAL:
			this.width = size;
			break;
		case VERTICAL:
			this.height = size;
			break;
		default:
			break;
		}
	}
	
	public void combine(Size other, Axis axis)
	{
		if (axis == null)
		{
			if (isConstrained(other.width))
				this.width = other.width;
			
			if (isConstrained(other.height))
				this.height = other.height;
			return;
		}
		
		switch (axis)
		{
		case HORIZONTAL:
			if (!isConstrained(this.width) || !isConstrained(other.width))
				this.width = UNCONSTRAINED;
			else
				this.width += other.width;

			if (isConstrained(other.height))
				this.height = other.height;
			break;
		case VERTICAL:
			if (!isConstrained(this.height) || !isConstrained(other.height))
				this.height = UNCONSTRAINED;
			else
				this.height += other.height;

			if (isConstrained(other.width))
				this.width = other.width;
			break;
		default:
			break;
		}
	}
	
	public static boolean isConstrained(int value)
	{
		return value >= 0;
	}
	
	@Override
	public String toString()
	{
		return this.width + " x " + this.height;
	}
}

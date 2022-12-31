package fr.max2.betterconfig.client.gui.layout;


public class Size
{
	public static final int UNCONSTRAINED = -1;
	
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
		return switch (axis)
		{
			case HORIZONTAL -> this.width;
			case VERTICAL -> this.height;
		};
	}
	
	public void set(Axis axis, int size)
	{
		switch (axis)
		{
			case HORIZONTAL -> this.width = size;
			case VERTICAL -> this.height = size;
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
			case HORIZONTAL ->
			{
				if (!isConstrained(this.width) || !isConstrained(other.width))
					this.width = UNCONSTRAINED;
				else
					this.width += other.width;

				if (isConstrained(other.height))
					this.height = other.height;
			}
			case VERTICAL ->
			{
				if (!isConstrained(this.height) || !isConstrained(other.height))
					this.height = UNCONSTRAINED;
				else
					this.height += other.height;

				if (isConstrained(other.width))
					this.width = other.width;
			}
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

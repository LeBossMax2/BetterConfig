package fr.max2.betterconfig.client.gui.layout;


public record Padding(int top, int right, int bottom, int left)
{
	public Padding()
	{
		this(0, 0, 0, 0);
	}
	
	public Size pad(Size s)
	{
		int w = Size.UNCONSTRAINED;
		if (Size.isConstrained(s.width))
		{
			w = s.width - this.right - this.left;
			if (w < 0) w = 0;
		}
		int h = Size.UNCONSTRAINED;
		if (Size.isConstrained(s.height))
		{
			h = s.height - this.top - this.bottom;
			if (h < 0) h = 0;
		}
		return new Size(w, h);
	}
	
	public Rectangle pad(Rectangle s)
	{
		return new Rectangle(s.x + this.left, s.y + this.top, pad(s.size));
	}
	
	public Size unpad(Size s)
	{
		int w = Size.UNCONSTRAINED;
		if (Size.isConstrained(s.width))
		{
			w = s.width + this.right + this.left;
			if (w < 0) w = 0;
		}
		int h = Size.UNCONSTRAINED;
		if (Size.isConstrained(s.height))
		{
			h = s.height + this.top + this.bottom;
			if (h < 0) h = 0;
		}
		return new Size(w, h);
	}
	
	public Rectangle unpad(Rectangle s)
	{
		return new Rectangle(s.x - this.left, s.y - this.top, unpad(s.size));
	}
}

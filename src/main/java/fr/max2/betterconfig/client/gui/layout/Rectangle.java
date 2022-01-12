package fr.max2.betterconfig.client.gui.layout;


public class Rectangle
{
	public final Size size;
	public int x, y;
	
	public Rectangle()
	{
		this(0, 0, new Size());
	}
	
	public Rectangle(int x, int y, int w, int h)
	{
		this(x, y, new Size(w, h));
	}
	
	public Rectangle(int x, int y, Size size)
	{
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	public int getPos(Axis axis)
	{
		switch (axis)
		{
		case HORIZONTAL:
			return this.x;
		case VERTICAL:
			return this.y;
		default:
			return -1;
		}
	}
	
	public void setPos(Axis axis, int pos)
	{
		switch (axis)
		{
		case HORIZONTAL:
			this.x = pos;
			break;
		case VERTICAL:
			this.y = pos;
			break;
		default:
			break;
		}
	}
	
	public int getLeft()
	{
		return this.x;
	}
	
	public int getRight()
	{
		return this.x + this.size.width;
	}
	
	public int getTop()
	{
		return this.y;
	}
	
	public int getBottom()
	{
		return this.y + this.size.height;
	}
	
	public int getCenterX()
	{
		return this.x + this.size.width / 2;
	}
	
	public int getCenterY()
	{
		return this.y + this.size.height / 2;
	}
	
	public boolean isPointInside(double x, double y)
	{
		return x >= this.getLeft()
		    && y >= this.getTop()
		    && x < this.getRight()
		    && y < this.getBottom();
	}
	@Override
	public String toString()
	{
		return "x: " + this.x + ", y: " + this.y + ", size: " + this.size;
	}
}

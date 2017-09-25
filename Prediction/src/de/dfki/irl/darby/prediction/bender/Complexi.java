package de.dfki.irl.darby.prediction.bender;

public class Complexi
{
	//Special Constants
	static public final Complexi zero 	= new Complexi(0, 0); 
	static public final Complexi one 	= new Complexi(1, 0); 
	static public final Complexi i 		= new Complexi(0, 1); 
	
	//Fields
	public int x, y;
	
	//Constructors
	public Complexi()
	{
		x = 0;
		y = 0;
	}
	public Complexi(int x)
	{
		this.x = x;
		this.y = 0;
	}
	public Complexi(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public Complexi(Complexi z)
    {
        this.x = z.x;
        this.y = z.y;
    }

	//Properties
	public Complexi Conjugated()
    {
        return new Complexi(x, -y);
    }
    public Complex Normalized()
    {
    	return new Complex(x, y).Normalize();
    }
    public double Magnitude()
    {
    	return Math.sqrt(x*x + y*y);
    }
    public int SqrMagnitude()
    {
    	return x * x + y * y;
    }
    public double Angle() //Measured in radians
    {
    	return Math.atan2(y, x);
    }
    
    //Methods 
    public Complexi Conjugate()
    {
        y = -y;
        return this;
    }
    public Complexi Set(Complexi z)
    {
    	x = z.x;
    	y = z.y;
    	return this;
    }
    public Complexi Set(int x, int y)
    {
    	this.x = x;
    	this.y = y;
    	return this; 	
    }
    
    public Complexi Add(Complexi z)
	{
    	x += z.x;
    	y += z.y;
		return this;
	}
	public Complexi Add(int r)
	{
		x += r;
		return this;
	}
	
	public Complexi Sub(Complexi z)
	{
    	x -= z.x;
    	y -= z.y;
		return this;
	}
	public Complexi Sub(int r)
	{
		x -= r;
		return this;
	}

	public Complexi Mul(Complexi z)
	{
		int aux = x;
    	x = z.x*aux - z.y*y;
    	y = z.y*aux + y*z.x;
		return this;
	}
	public Complexi Mul(int r)
	{
		x *= r;
    	y *= r;
		return this;
	}

	public Complexi Div(Complexi z)
	{
		int k = z.SqrMagnitude();
		int aux = x;
    	x = (z.x*aux + z.y*y)/k;
    	y = (-z.y*aux + y*z.x)/k;
		return this;
	}
	public Complexi Div(int r)
	{
		x /= r;
    	y /= r;
		return this;
	}
	
	public Complexi Neg()
	{
		x = -x;
		y = -y;
		return this;
	}
	
    //Operations
	static public Complexi Add(Complexi a, Complexi b)
	{
		return new Complexi(a.x + b.x, a.y + b.y);
	}
	static public Complexi Add(Complexi a, int b)
	{
		return new Complexi(a.x + b, a.y);
	}
	static public Complexi Add(int a, Complexi b)
	{
		return new Complexi(b.x + a, b.y);
	}
	
	static public Complexi Sub(Complexi a, Complexi b)
	{
		return new Complexi(a.x - b.x, a.y - b.y);
	}
	static public Complexi Sub(Complexi a, int b)
	{
		return new Complexi(a.x - b, a.y);
	}
	static public Complexi Sub(int a, Complexi b)
	{
		return new Complexi(a - b.x, -b.y);
	}

	static public Complexi Mul(Complexi a, Complexi b)
	{
		return new Complexi(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x);
	}
	static public Complexi Mul(Complexi a, int b)
	{
		return new Complexi(a.x*b, a.y*b);
	}
	static public Complexi Mul(int a, Complexi b)
	{
		return new Complexi(b.x*a, b.y*a);
	}
	
	static public Complexi Div(Complexi a, Complexi b)
	{
		int k = b.SqrMagnitude();
		return new Complexi((a.x*b.x + a.y*b.y)/k, (-a.x*b.y + a.y*b.x)/k);
	}
	static public Complexi Div(Complexi a, int b)
	{
		return new Complexi(a.x/b, a.y/b);
	}
	static public Complexi Div(int a, Complexi b)
	{
		int k = b.SqrMagnitude();
		return new Complexi(a*b.x/k, -a*b.y/k);
	}
	
	static public Complexi Neg(Complexi z)
	{
		return new Complexi(-z.x, -z.y);
	}
	
	//Auxiliary
	@Override
    public String toString()
    {
        String str = Integer.toString(x);
        if (y > 0)
        {
            str += "+" + Integer.toString(y) + "i";
        }
        else if (y < 0)
        {
            str += Integer.toString(y) + "i";
        }

        return str;
    }
}
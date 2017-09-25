package de.dfki.irl.darby.prediction.bender;

public class Complex
{
	//Special Constants
	static public final Complex zero 	= new Complex(0, 0); 
	static public final Complex one 	= new Complex(1, 0); 
	static public final Complex i 		= new Complex(0, 1); 
	
	//Fields
	public double x, y;
	
	//Constructors
	public Complex()
	{
		x = 0;
		y = 0;
	}
	public Complex(double x)
	{
		this.x = x;
		this.y = 0;
	}
	public Complex(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public Complex(Complex z)
    {
        this.x = z.x;
        this.y = z.y;
    }

	//Properties
	public Complex Conjugated()
    {
        return new Complex(x, -y);
    }
    public Complex Normalized()
    {
    	return new Complex(this).Normalize();
    }
    public double Magnitude()
    {
    	return Math.sqrt(x*x + y*y);
    }
    public double SqrMagnitude()
    {
    	return x * x + y * y;
    }
    public double Angle() //Measured in radians
    {
    	return Math.atan2(y, x);
    }
    
    public double len(){
    	return Math.sqrt(x*x+y*y);
    }
    //Methods 
    public Complex Normalize()
    {
        double r = this.Magnitude();
        x /= r;
        y /= r;

        return this;
    }
    public Complex Conjugate()
    {
        y = -y;
        return this;
    }
    public Complex Set(Complex z)
    {
    	x = z.x;
    	y = z.y;
    	return this;
    }
    public Complex Set(double x, double y)
    {
    	this.x = x;
    	this.y = y;
    	return this; 	
    }
    
    public Complex Add(Complex z)
	{
    	x += z.x;
    	y += z.y;
		return this;
	}
	public Complex Add(double r)
	{
		x += r;
		return this;
	}
	
	public Complex Sub(Complex z)
	{
    	x -= z.x;
    	y -= z.y;
		return this;
	}
	public Complex Sub(double r)
	{
		x -= r;
		return this;
	}

	public Complex Mul(Complex z)
	{
		double aux = x;
    	x = z.x*aux - z.y*y;
    	y = z.y*aux + y*z.x;
		return this;
	}
	public Complex Mul(double r)
	{
		x *= r;
    	y *= r;
		return this;
	}

	public Complex Div(Complex z)
	{
		double k = z.SqrMagnitude();
		double aux = x;
    	x = (z.x*aux + z.y*y)/k;
    	y = (-z.y*aux + y*z.x)/k;
		return this;
	}
	public Complex Div(double r)
	{
		x /= r;
    	y /= r;
		return this;
	}
	
	public Complex Neg()
	{
		x = -x;
		y = -y;
		return this;
	}
	
    //Operations
	static public Complex Add(Complex a, Complex b)
	{
		return new Complex(a.x + b.x, a.y + b.y);
	}
	static public Complex Add(Complex a, double b)
	{
		return new Complex(a.x + b, a.y);
	}
	static public Complex Add(double a, Complex b)
	{
		return new Complex(b.x + a, b.y);
	}
	
	static public Complex Sub(Complex a, Complex b)
	{
		return new Complex(a.x - b.x, a.y - b.y);
	}
	static public Complex Sub(Complex a, double b)
	{
		return new Complex(a.x - b, a.y);
	}
	static public Complex Sub(double a, Complex b)
	{
		return new Complex(a - b.x, -b.y);
	}

	static public Complex Mul(Complex a, Complex b)
	{
		return new Complex(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x);
	}
	static public Complex Mul(Complex a, double b)
	{
		return new Complex(a.x*b, a.y*b);
	}
	static public Complex Mul(double a, Complex b)
	{
		return new Complex(b.x*a, b.y*a);
	}
	
	static public Complex Div(Complex a, Complex b)
	{
		double k = b.SqrMagnitude();
		return new Complex((a.x*b.x + a.y*b.y)/k, (-a.x*b.y + a.y*b.x)/k);
	}
	static public Complex Div(Complex a, double b)
	{
		return new Complex(a.x/b, a.y/b);
	}
	static public Complex Div(double a, Complex b)
	{
		double k = b.SqrMagnitude();
		return new Complex(a*b.x/k, -a*b.y/k);
	}
	
	static public Complex Neg(Complex z)
	{
		return new Complex(-z.x, -z.y);
	}
	

	static public double Dot(Complex a, Complex b)
	{
		return a.x*b.x + a.y*b.y;
	}
	static public Complex Proj(Complex a, Complex b)
	{
		return Complex.Mul(Complex.Dot(a, b)/b.SqrMagnitude(), b);
	}
	
	//Auxiliary
	@Override
    public String toString()
    {
        String str = Double.toString(x);
        if (y > 0)
        {
            str += "+" + Double.toString(y) + "i";
        }
        else if (y < 0)
        {
            str += Double.toString(y) + "i";
        }

        return str;
    }
}
package de.dfki.irl.darby.prediction.bender;

import de.dfki.irl.darby.prediction.accumulation.Globals;

public class PlotWindow
{
	public enum Space
	{
		Screen,
		World
	}
	
	public Complex worldPosition = new Complex(0, 0), 
				   worldSize =new Complex(1, 1);

	public Complex screenPosition = new Complex(0, 0),
				   screenSize = new Complex(1, 1);
	
	public Complex MapScreenToWorld(Complex point)
	{
		return new Complex(worldPosition.x + ((point.x - screenPosition.x) / screenSize.x) * worldSize.x,
						   worldPosition.y + (1.0 - (point.y - screenPosition.y) / screenSize.y) * worldSize.y);
	}
	
	public Complex MapWorldToScreen(Complex point)
	{
		return new Complex(screenPosition.x + ((point.x - worldPosition.x) / worldSize.x) * screenSize.x,
						   screenPosition.y + (1.0 - (point.y - worldPosition.y) / worldSize.y) * screenSize.y);
	}
	
	public Complex MapWorldToScreen(double x, double y)
	{
		return new Complex(screenPosition.x + ((x - worldPosition.x) / worldSize.x) * screenSize.x,
						   screenPosition.y + (1.0 - (y - worldPosition.y) / worldSize.y) * screenSize.y);
	}
	
	public boolean IsInside (Complex point, Space space)
	{
		if (space == Space.World)
		{
			return point.x >= worldPosition.x && point.y > worldPosition.y &&
				   point.x < worldPosition.x + worldSize.x && point.y <= worldPosition.y + worldSize.y;
		}
		else
		{
			return point.x >= screenPosition.x && point.y >= screenPosition.y &&
				   point.x < screenPosition.x + screenSize.x && point.y < screenPosition.y + screenSize.y;
		}
	}
	
	public void Translate (Complex delta, Space space)
	{
		if (space == Space.Screen)
		{
			worldPosition.Add(MapScreenToWorld(delta).Sub(new Complex(worldPosition.x, worldPosition.y + worldSize.y)));
		}
		else
		{
			worldPosition.Add(delta);
		}
	}
	
	public void LookAt (Complex center, Space space)
	{
		Complex position;
		if (space == Space.Screen)
		{
			position = MapScreenToWorld(center);
		}
		else
		{
			position = center;
		}
		
		worldPosition.Set(position.Sub(Complex.Div(worldSize, 2)));	
	}

	public void Zoom(double factor, Complex center, Space centerSpace)
	{
		Complex worldCenter;
		
		if (centerSpace == Space.Screen)
		{
			worldCenter = MapScreenToWorld(center);
		}
		else
		{
			worldCenter = center;
		}
		
		worldPosition.x = worldCenter.x - (worldCenter.x - worldPosition.x)/factor;
		worldPosition.y = worldCenter.y - (worldCenter.y - worldPosition.y)/factor;
		
		worldSize.Div(factor);
	}

	public void Zoom(double factor)
	{
		Complex worldCenter = new Complex(worldPosition.x + worldSize.x / 2.0, worldPosition.y + worldSize.y / 2.0);
		worldPosition.x = worldCenter.x - (worldCenter.x - worldPosition.x)/factor;
		worldPosition.y = worldCenter.y - (worldCenter.y - worldPosition.y)/factor;
		worldSize.Div(factor);
	}
	
	public void SetWindow(Complex A, Complex B, Space space)
	{
		if (space == Space.Screen)
		{
			A = MapScreenToWorld(A);
			B = MapScreenToWorld(B);
		}

		if (A.x > B.x)
		{
			double aux = A.x;
			A.x = B.x;
			B.x = aux;
		}
		
		if (A.y > B.y)
		{
			double aux = A.y;
			A.y = B.y;
			B.y = aux;
		}
		
		worldPosition.Set(A);
		worldSize.Set(Complex.Sub(B, A));
	}
	
	
	public double MapScreenXToWorldX(double pointX)
	{
		return worldPosition.x + ((pointX - screenPosition.x) / screenSize.x) * worldSize.x;
	}
	
	public double MapWorldXToScreenX(double pointX)
	{
		return screenPosition.x + ((pointX - worldPosition.x) / worldSize.x) * screenSize.x;			 
	}

	public void TranslateX (double deltaX, Space space)
	{
		if (space == Space.Screen)
		{
			worldPosition.x += MapScreenXToWorldX(deltaX) - worldPosition.x;
		}
		else
		{
			worldPosition.x += deltaX;
		}
	}
	
	public void LookAtX (double centerX, Space space)
	{
		double position;
		if (space == Space.Screen)
		{
			position = MapScreenXToWorldX(centerX);
		}
		else
		{
			position = centerX;
		}
		
		worldPosition.x = (position - worldSize.x/2);	
	}

	public void ZoomX(double factor, double center, Space centerSpace)
	{
		double worldCenter;
		
		if (centerSpace == Space.Screen)
		{
			worldCenter = MapScreenXToWorldX(center);
		}
		else
		{
			worldCenter = center;
		}
		
		worldPosition.x = worldCenter - (worldCenter - worldPosition.x)/factor;
		worldSize.x /= factor;
	}

	public void ZoomX(double factor)
	{
		double worldCenter = worldPosition.x + worldSize.x / 2.0;
		worldPosition.x = worldCenter - (worldCenter - worldPosition.x)/factor;
		worldSize.x /= factor;
	}

	public void SetWindowX(double A, double B, Space space)
	{
		if (space == Space.Screen)
		{
			A = MapScreenXToWorldX(A);
			B = MapScreenXToWorldX(B);
		}

		if (A > B)
		{
			double aux = A;
			A = B;
			B = aux;
		}
		
		worldPosition.x = A;
		worldSize.x = B - A;
	}

	
	public double MapScreenYToWorldY(double point)
	{
		return worldPosition.y + (1.0 - (point - screenPosition.y) / screenSize.y) * worldSize.y;
	}
	
	public double MapWorldYToScreenY(double point)
	{
		return screenPosition.y + (1.0 - (point - worldPosition.y) / worldSize.y) * screenSize.y;
	}

	public void TranslateY (double delta, Space space)
	{
		if (space == Space.Screen)
		{
			worldPosition.y += MapScreenYToWorldY(delta) -(worldPosition.y + worldSize.y);
		}
		else
		{
			worldPosition.y += delta;
		}
	}
	
	public void LookAtY (double center, Space space)
	{
		double position;
		if (space == Space.Screen)
		{
			position = MapScreenYToWorldY(center);
		}
		else
		{
			position = center;
		}
		
		worldPosition.y = position - worldSize.y / 2;	
	}

	public void ZoomY(double factor, double center, Space centerSpace)
	{
		double worldCenter;
		
		if (centerSpace == Space.Screen)
		{
			worldCenter = MapScreenYToWorldY(center);
		}
		else
		{
			worldCenter = center;
		}
		
		worldPosition.y = worldCenter - (worldCenter - worldPosition.y)/factor;
		worldSize.y /= factor;
	}

	public void ZoomY(double factor)
	{
		double worldCenter = worldPosition.y + worldSize.y / 2.0;
		worldPosition.y = worldCenter - (worldCenter - worldPosition.y)/factor;
		worldSize.y /= factor;
	}
	
	public void SetWindowY(double A, double B, Space space)
	{
		if (space == Space.Screen)
		{
			A = MapScreenYToWorldY(A);
			B = MapScreenYToWorldY(B);
		}
		
		if (A > B)
		{
			double aux = A;
			A = B;
			B = aux;
		}
		
		worldPosition.y = A;
		worldSize.y = B - A;
	}

	public void ResizeScreenKeepingRatios(double sizeX, double sizeY)
	{
		double aux = worldSize.y;
		worldSize.Set((worldSize.x/screenSize.x)*sizeX, (worldSize.y/screenSize.y)*sizeY);
		worldPosition.y += aux - worldSize.y;
		screenSize.Set(sizeX, sizeY);
	}

	public void MakeOneToOneRatios()
	{
		Complex upLeft = new Complex(worldPosition.x, worldPosition.y + worldSize.y);
		Complex downRight = new Complex(worldPosition.x + worldSize.x, worldPosition.y);
		Complex diretiction = screenSize.Conjugated();
		downRight = Complex.Proj(downRight.Sub(upLeft), diretiction).Add(upLeft);
		
		worldPosition.y = downRight.y;
		worldSize.Set(downRight.x - upLeft.x, upLeft.y - downRight.y);
	}
}

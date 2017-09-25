package de.dfki.irl.darby.prediction.bender;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;

public class Plotter extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, ComponentListener
{
	ArrayList<UPurchase> upurchases=new ArrayList<UPurchase>();
	
	public ArrayList<UPurchase> getUpurchases() {
		return upurchases;
	}

	public void setUpurchases(ArrayList<UPurchase> upurchases) {
		this.upurchases = upurchases;
	}
	PlotWindow plotWindow = new PlotWindow();
	Color colorGridLines = new Color(0xdd, 0xdd, 0xdd); 
	Color colorBackground = new Color(0xff, 0xff, 0xff);
	Color colorGridValues = new Color(63, 72, 204); 
	Color colorMousePointValueColor = new Color(0, 0, 0); 
	Color colorBorderSelectionColor = new Color(51, 153, 255); 
	Color colorInnerSelectionColor  = new Color(51, 153, 255, 50); 
	Color colorMeasurement  = new Color(255, 0, 128); 
	
	Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	boolean isSelecting = false;
	boolean isSelected = false;
	boolean isTranslating = false;
	boolean showPointValue = false;
	Complex initialSelectionPoint = new Complex(); //World points
	Complex finalSelectionPoint = new Complex();
	Complex initialTranslationPoint = new Complex();
	Complex finalTranslationPoint = new Complex();
	long lastButton1ClickTime = System.currentTimeMillis();
	long doubleClickDeltaTime = 450;
	
	double zoomFactorPerWheelRotation = 0.80;
	
	int gridSpacingX = 30,
		gridSpacingY = 30;
	
	static Color  defaultLineColor = new Color(0, 0, 0);
	static Stroke defaultLineStroke = new BasicStroke();

	List<IPlottable> plottables = new LinkedList<IPlottable>();
	
	public Plotter()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addKeyListener(this);
		this.addComponentListener(this);
		this.setFocusable(true);
	}
	
	public void Clear()
	{
		plottables.clear();
	}

	public void Add(IPlottable plottable)
	{
		plottables.add(plottable);
	}

	@Override
	public void keyReleased(KeyEvent arg0) 
	{
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		switch(arg0.getKeyCode())
		{
			case KeyEvent.VK_ENTER: 
				plotWindow.SetWindow(initialSelectionPoint, finalSelectionPoint, PlotWindow.Space.World);
				repaint();
				break;
				
			case KeyEvent.VK_SPACE:
				showPointValue = !showPointValue;
				repaint();
				break;
				
			case KeyEvent.VK_C:
				if (arg0.isControlDown())
				{
					Point mousePosition = this.getMousePosition();
					if (mousePosition != null)
					{
						Complex position = plotWindow.MapScreenToWorld(new Complex(mousePosition.getX(), mousePosition.getY()));
						String myString = Double.toString(position.x) + " " + Double.toString(position.y);
				    	StringSelection stringSelection = new StringSelection (myString);
				    	Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				    	clipboard.setContents (stringSelection, null);
					}
				}
				break;	
				
			case KeyEvent.VK_ESCAPE:
				isSelected = false;
				repaint();
				break;
				
			case KeyEvent.VK_R:
				plotWindow.MakeOneToOneRatios();
				repaint();
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) 
	{
		double zoomFactor = Math.pow(zoomFactorPerWheelRotation, arg0.getWheelRotation());
		
		if (arg0.isControlDown())
		{
			double zoomCenter = arg0.getX();
			plotWindow.ZoomX(zoomFactor, zoomCenter, PlotWindow.Space.Screen);
		}
		else if (arg0.isShiftDown())
		{
			double zoomCenter = arg0.getY();
			plotWindow.ZoomY(zoomFactor, zoomCenter, PlotWindow.Space.Screen);
		}
		else
		{
			Complex zoomCenter = new Complex(arg0.getX(), arg0.getY());
			plotWindow.Zoom(zoomFactor, zoomCenter, PlotWindow.Space.Screen);
		}
		
		repaint();
	}
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		this.requestFocusInWindow();
		
		if (arg0.getButton() == arg0.BUTTON1)
		{
			if (isSelected)
			{
				long time = System.currentTimeMillis();
				if (time - lastButton1ClickTime < doubleClickDeltaTime)
				{
					plotWindow.SetWindow(initialSelectionPoint, finalSelectionPoint, PlotWindow.Space.World);
				}
				lastButton1ClickTime = time;
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		this.requestFocus();
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		if (arg0.getButton() == arg0.BUTTON1)
		{
			isTranslating = true;
			initialTranslationPoint.Set(arg0.getX(), arg0.getY());
		}
		else if (arg0.getButton() == arg0.BUTTON3)
		{
			if (!isSelecting)
			{
				isSelected = true;
				isSelecting = true;
				initialSelectionPoint = plotWindow.MapScreenToWorld(new Complex(arg0.getX(), arg0.getY()));
				finalSelectionPoint.Set(initialSelectionPoint);
			}
			else
			{
				isSelecting = false;
				finalSelectionPoint = plotWindow.MapScreenToWorld(new Complex(arg0.getX(), arg0.getY()));
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		if (arg0.getButton() == arg0.BUTTON1)
		{
			isTranslating = false;
			repaint();
		}
	}
	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		if (isTranslating)
		{
			finalTranslationPoint.Set(arg0.getX(), arg0.getY());
			Complex delta = Complex.Sub(initialTranslationPoint, finalTranslationPoint);
			plotWindow.Translate(delta, PlotWindow.Space.Screen);
			initialTranslationPoint.Set(finalTranslationPoint);
		}
		

		if (isSelecting)
		{
			finalSelectionPoint = plotWindow.MapScreenToWorld(new Complex(arg0.getX(), arg0.getY()));
		}

		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		if (isSelecting)
		{
			finalSelectionPoint = plotWindow.MapScreenToWorld(new Complex(arg0.getX(), arg0.getY()));
		}
		repaint();
	}
	
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) 
	{
		plotWindow.ResizeScreenKeepingRatios(this.getWidth(), this.getHeight());
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void paintBackground(Graphics2D g2) 
	{
		g2.setColor(colorBackground);
	    g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	public void paintGrid(Graphics2D g2) 
	{
		int borderX = this.getWidth() - 1;
		int borderY = this.getHeight() - 1;
		g2.setColor(colorGridLines);
		//g2.setStroke(dashedStroke);
		
		double gridWorldSpacingY = gridSpacingY*plotWindow.worldSize.y/plotWindow.screenSize.y;
	    int initialY = (int) plotWindow.MapWorldYToScreenY( ((int)((plotWindow.worldPosition.y + plotWindow.worldSize.y)/gridWorldSpacingY) + 1)*gridWorldSpacingY);
	    for (int y = initialY; y < borderY; y +=  gridSpacingY)
	    {
	    	g2.drawLine(0, y, borderX, y);
	    }
		
		double gridWorldSpacingX = gridSpacingX*plotWindow.worldSize.x/plotWindow.screenSize.x;
	    int initialX = (int) plotWindow.MapWorldXToScreenX( ((int)(plotWindow.worldPosition.x/gridWorldSpacingX) + 1)*gridWorldSpacingX);
	    for (int x = initialX; x < borderX; x +=  gridSpacingX)
	    {
	    	g2.drawLine(x, 0, x, borderY);
	    }
	}
	
	public void paintGridValues(Graphics2D g2) 
	{
		double value;
		String valueString;
		int borderX = this.getWidth() - 1;
		int borderY = this.getHeight() - 4;
		
		g2.setColor(colorGridValues);
		
		DecimalFormat formatter = new DecimalFormat("0.###E0");

		for (int x = gridSpacingX; x < borderX; x += 4*gridSpacingX)
	    {
			value = plotWindow.MapScreenXToWorldX(x);
			valueString = formatter.format(value);
	    	g2.drawString(valueString, x + 1, borderY);
	    }

	    for (int y = this.getHeight() - gridSpacingY; y > 0; y -=  2*gridSpacingY)
	    {
	    	value = plotWindow.MapScreenYToWorldY(y);
			valueString = formatter.format(value);
	    	g2.drawString(valueString, 2, y - 1);
	    }
	}
	
	public void paintMouseValueOverMouse(Graphics2D g2) 
	{
		FontMetrics fontMetrics = g2.getFontMetrics();
		Point mousePosition = this.getMousePosition();
		
		if (mousePosition == null)
			return;
		
		double valueX = plotWindow.MapScreenXToWorldX(mousePosition.getX());
		double valueY = plotWindow.MapScreenYToWorldY(mousePosition.getY());
		int mouseX = (int) mousePosition.getX();
		int mouseY = (int) mousePosition.getY();
		
		String valueStringX;
		String valueStringY;
		
		g2.setColor(colorMousePointValueColor);
		
		DecimalFormat formatter = new DecimalFormat("0.###############E0");

		valueStringX = "(" + formatter.format(valueX) + ",";
		valueStringY = " " + formatter.format(valueY) + ")";
    	g2.drawString(valueStringX, mouseX - fontMetrics.stringWidth(valueStringX), mouseY - 4);
    	g2.drawString(valueStringY, mouseX, mouseY - 4);
	}
	
	public void paintMouseValueAtScreenCorner(Graphics2D g2) 
	{
		FontMetrics fontMetrics = g2.getFontMetrics();
		Point mousePosition = this.getMousePosition();
		
		if (mousePosition == null)
			return;
		
		double valueX = plotWindow.MapScreenXToWorldX(mousePosition.getX());
		double valueY = plotWindow.MapScreenYToWorldY(mousePosition.getY());

		String valueString;
		
		g2.setColor(colorMousePointValueColor);
		
		DecimalFormat formatter = new DecimalFormat("0.###############E0");

		valueString = "(" + formatter.format(valueX) + ", " + formatter.format(valueY) + ")";
    	g2.drawString(valueString, this.getWidth() - fontMetrics.stringWidth(valueString) - 4, fontMetrics.getHeight() + 4);
	}
	
	public void paintSelectionArea(Graphics2D g2) 
	{
		Complexi corner = new Complexi(), 
				 size = new Complexi();
		
		Complex A = plotWindow.MapWorldToScreen(initialSelectionPoint), 
				B = plotWindow.MapWorldToScreen(finalSelectionPoint);

		if (A.x < B.x)
		{
			corner.x = (int) A.x;
			size.x = (int) (B.x - A.x);
		}
		else
		{
			corner.x = (int)  B.x;
			size.x = (int) (A.x - B.x);
		}
		
		if (A.y < B.y)
		{
			corner.y = (int) A.y;
			size.y = (int) (B.y - A.y);
		}
		else
		{
			corner.y = (int)  B.y;
			size.y = (int) (A.y - B.y);
		}
		
		g2.setColor(colorInnerSelectionColor);
		g2.fillRect(corner.x, corner.y, size.x, size.y);
		
		g2.setColor(colorBorderSelectionColor);
		g2.drawRect(corner.x, corner.y, size.x, size.y);
	}

	public void paintMeasurementLine(Graphics2D g2) 
	{
		Complex A = plotWindow.MapWorldToScreen(initialSelectionPoint), 
				B = plotWindow.MapWorldToScreen(finalSelectionPoint),
				center = Complex.Div(Complex.Add(A, B), 2);
		
		Complexi Ai = new Complexi((int) A.x, (int) A.y),
				 Bi = new Complexi((int) B.x, (int) B.y),
				 centeri = new Complexi((int) center.x, (int) center.y);
		
		double distance = Complex.Sub(finalSelectionPoint, initialSelectionPoint).Magnitude();
		
		g2.setColor(colorBorderSelectionColor);
		g2.drawLine(Ai.x, Ai.y, Bi.x, Bi.y);
		
		
		
		
		DecimalFormat formatter = new DecimalFormat("0.###############E0");
		String distanceStr = formatter.format(distance);

		FontMetrics fontMetrics = g2.getFontMetrics();
		centeri.x -= fontMetrics.stringWidth(distanceStr)/2;
		centeri.y -= 2;
		
		g2.setColor(colorMeasurement);
		g2.drawString(distanceStr,centeri.x, centeri.y);
	
	}
	
	public void paintPlottables(Graphics2D g2) 
	{
		for (IPlottable p : plottables)
		{
			p.plot(g2, plotWindow);
		}
	}

	public void paintComponent(Graphics g) 
	{
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;

	    paintBackground(g2);
	    paintGrid(g2);
	    
	    
	    if (showPointValue)
	    	paintMouseValueAtScreenCorner(g2);
	    
	    paintPlottables(g2);
	    
	    
	    if (isSelected)
	    {
	    	paintMeasurementLine(g2);
	    	//paintSelectionArea(g2);
	    }
	    
	    paintGridValues(g2);
	    
	}
	

}

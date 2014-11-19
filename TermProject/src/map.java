//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;

//import javax.swing.text.html.HTMLDocument.Iterator;

public class map {
	
	int size;
	BufferedImage mapObject = null;
	BufferedImage BACKGROUND = null;
	Graphics2D g2d;
	Random rand = new Random();
	ArrayList<Actor> actors;
	int CELLSIZE;
	

	public map(int sizeIn, int CELLSIZE)//400 = 25x25, 800 = 50x50
	{
		this.CELLSIZE = CELLSIZE;
		Random rand = new Random();
		actors = new ArrayList<Actor>();
		size = sizeIn+1;
		//make the map
		BACKGROUND = new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB);
		//mapObject = new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)BACKGROUND.getGraphics();
		
		//set as white
		for (int i = 0; i <= size-1; i++)
		{
			for (int j = 0; j <= size-1; j++)
			{
				//set all pixels as white
				int color = rand.nextInt(3);
				int colorInt = 0;
				if (color == 0)
				{
					colorInt = 0xFF189a2e;
				}
				else if (color == 1)
				{
					colorInt = 0xFF18922c;
				}
				else
				{
					colorInt = 0xFF199f2f;
				}
				if (i%CELLSIZE == 0 || j%CELLSIZE == 0)
				{
					colorInt = 0xff136722;
				}
				BACKGROUND.setRGB(i,j,colorInt);
			}
		}	
		mapObject = deepCopy(BACKGROUND);
		g2d = (Graphics2D)mapObject.getGraphics();
		g.dispose();
		Actor testBunny = new Bunny(5,11);
		Actor testBunny2 = new Bunny(7,5);
		Actor testBunny3 = new Bunny(11,2);
		Actor testBunny4 = new Bunny(24,24);
		Actor testBunny5 = new Bunny(10,15);
		for (int j = 7;j < 18; j+=2)
		{
			for (int i = 7; i < 18; i++)
			{
				Actor testFoodTemp = new Food(j,i);
				actors.add(testFoodTemp);
			}
		}
		actors.add(testBunny);
		actors.add(testBunny2);
		actors.add(testBunny3);
		actors.add(testBunny4);
		actors.add(testBunny5);
		
		//Astar a= new Astar();
		//a.pathfindBreadthFirst(new Point2D.Double(0,0), new Point2D.Double(2,4), this);
		
		render();
	}
	
	public BufferedImage getMap()
	{
		//return map
		return mapObject;
	}
	
	public void act()
	{
		for (Actor a : actors)
		{
			a.act(this, actors);
		}
		//remove dead actors
		for (final java.util.Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); )
		{
			Actor a = iterator.next();
			if (a.isDead())
			{
				iterator.remove();
			}
		}
		//System.out.println(actors.size());
		
		render();
	}
	
	public void render()
	{
		mapObject = deepCopy(BACKGROUND);
		
		for (Actor a : actors)
		{
			a.render(g2d);
		}
	}
	
	private BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 BufferedImage temp = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		 g2d = (Graphics2D)temp.getGraphics();
		 return temp;
	}
	
	
	
}

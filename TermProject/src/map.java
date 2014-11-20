//import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.geom.Point2D;
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
	ArrayList<Home> homes;
	int CELLSIZE;
	boolean WINNER = false;

	public map(int sizeIn, int CELLSIZE)//400 = 25x25, 800 = 50x50
	{
		this.CELLSIZE = CELLSIZE;
		Random rand = new Random();
		actors = new ArrayList<Actor>();
		homes = new ArrayList<Home>();
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
		//Actor testBunny = new Bunny(3,3);
		//Actor testBunny2 = new Bunny(7,5);
		//Actor testBunny3 = new Bunny(11,2);
		//Actor testBunny4 = new Bunny(24,24);
		//Actor testBunny5 = new Bunny(10,15);
		Actor testHomeNW = new Home(0,0,4);
		Actor testHomeNE = new Home(24,0,4);
		Actor testHomeSW = new Home(0,24,4);
		Actor testHomeSE = new Home(24,23,4);
		for (int j = 7;j < 18; j+=2)
		{
			for (int i = 7; i < 18; i+=2)
			{
				Actor testFoodTemp = new Food(j,i);
				actors.add(testFoodTemp);
			}
		}
		//actors.add(testBunny);
		//actors.add(testBunny2);
		//actors.add(testBunny3);
		//actors.add(testBunny4);
		//actors.add(testBunny5);
		actors.add(testHomeNW);
		homes.add((Home)testHomeNW);
		actors.add(testHomeNE);
		homes.add((Home)testHomeNE);
		actors.add(testHomeSW);
		homes.add((Home)testHomeSW);
		actors.add(testHomeSE);
		homes.add((Home)testHomeSE);
		//((Home)testHomeNW).active = false;
		//((Home)testHomeNE).active = false;
		//((Home)testHomeSW).active = false;
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
		//CHECK FOR WIN CONDITION
		if (!WINNER)
		{
			boolean winner = true;
			for (Actor a : actors)
			{
				if (a.getTYPE() == actorTYPE.FOOD)
				{
					winner = false;
				}
			}
			
			if (winner)
			{
				int agg = 0;
				//recall all bunnies from HOMES
				for (Home h : homes)
				{
					h.recall();
					agg += h.number_of_bunnies_spawned;
				}
				WINNER = true;
				System.out.println("Number of bunnies used: " + agg);
			}
		}
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
	
	public boolean addActor(int x, int y, actorTYPE a)
	{
		Actor act = null;
		switch (a)
		{
		case FENCE:
			act = new Fence(x,y);
			break;
		
			
		}
		if (act != null)
		{
			actors.add(act);
			render();
			return true;
		}
		return false;
	}
	
	public boolean occupied(int x, int y)
	{
		for (Actor a : actors)
		{
			if (a.getXY()[0] == x && a.getXY()[1] == y)
			{
				return true;
			}
		}
		return false;
		
	}
	
	public boolean occupiedExclusion(int x, int y,Actor ex)
	{
		for (Actor a : actors)
		{
			if (a.getXY()[0] == x && a.getXY()[1] == y && a != ex)
			{
				return true;
			}
		}
		return false;
		
	}
	
}

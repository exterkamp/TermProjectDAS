//import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
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
	int fight;
	int flight;
	int hunger;
	int courage;
	

	public map(int sizeIn, int CELLSIZE, int[] stats)//400 = 25x25, 800 = 50x50
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
		//set stats
		fight = stats[0];
		flight = stats[1];
		hunger = stats[2];
		courage = stats[3];
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
		Actor testHomeNW = new Home(0,0,2);
		Actor testHomeNE = new Home(24,0,2);
		Actor testHomeSW = new Home(0,24,2);
		Actor testHomeSE = new Home(24,24,2);
		Actor testFox = new Fox(12,12);
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
		//always last!?
		actors.add(testFox);
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
			else if(a.getTYPE() == actorTYPE.HOME)
			{
				//scan through home's stuff
				Home h = (Home)a;
				for (final Iterator<Bunny> iteratorInner = h.children.iterator(); iteratorInner.hasNext(); )
				{
					Bunny b = iteratorInner.next();
					if (b.isDead())
					{
						//removing dead bunny
						iteratorInner.remove();
					}
				}
				
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
				for (Actor a : actors)
				{
					if (a.getTYPE() == actorTYPE.HOME)
					{
						((Home)a).recall();
						agg += ((Home)a).number_of_bunnies_spawned;
					}
					else if (a.getTYPE() == actorTYPE.FOX)
					{
						System.out.println("Number of bunnies lost: " + ((Fox)a).murders);
					}
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
			a.render(g2d, CELLSIZE);
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
		default:
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
			if (a.getTYPE() == actorTYPE.HOME)
			{
				for (Actor ayyLmao : ((Home)a).children)
				{
					if (ayyLmao.getXY()[0] == x && ayyLmao.getXY()[1] == y)
					{
						return true;
					}
				}
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
	
	public Actor occupiedActorReturn(int x, int y)
	{
		for (Actor a : actors)
		{
			if (a.getXY()[0] == x && a.getXY()[1] == y)
			{
				return a;
			}
			if (a.getTYPE() == actorTYPE.HOME)
			{
				for (Actor ayyLmao : ((Home)a).children)
				{
					//System.out.println("looking at a bunny");
					if (ayyLmao.getXY()[0] == x && ayyLmao.getXY()[1] == y)
					{
						//System.out.println("found an actor");
						return ayyLmao;//can find actors
					}
				}
			}
		}
		return null;
		
	}
	public void changeStat(int num, int val)//1 = fight, 2 = flight, 3 = hunger, 4 = courage
	{
		switch (num)
		{
		case 1:
			fight = val;
			break;
		case 2:
			flight = val;
			break;
		case 3:
			hunger = val;
			break;
		case 4:
			courage = val;
			break;
			
		}
	}
	
	public int[] getStats()
	{
		int[] array = {fight,flight,hunger,courage};
		return array;
	}
	
	
}

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
	//ArrayList<Home> homes;
	int CELLSIZE;
	boolean WINNER = false;
	int overlay; //0 is no overlay, 1 is path overlay
	
	int fight;
	int flight;
	int hunger;
	int courage;
	int plant_avg_x, plant_avg_y;
	//the map object in node form
	mapNode[][] nodes;
	
	
	

	public map(int sizeIn, int CELLSIZE, int[] stats)//400 = 25x25, 800 = 50x50
	{
		overlay = 0;
		this.CELLSIZE = CELLSIZE;
		Random rand = new Random();
		actors = new ArrayList<Actor>();
		nodes = new mapNode[25][25];
		plant_avg_x = 12;
		plant_avg_y = 12;
		for (int row = 0;row < 25;row++)
		{
			for (int col = 0;col < 25;col++)
			{
				nodes[row][col] = new mapNode(row,col);
			}
		}
		//homes = new ArrayList<Home>();
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

		Actor testHomeNW = new Home(0,0,1);
		Actor testHomeNE = new Home(24,0,1);
		Actor testHomeSW = new Home(0,24,1);
		Actor testHomeSE = new Home(24,24,1);
		Actor testFox = new Fox(12,12);
		for (int j = 7;j < 18; j+=2)
		{
			for (int i = 7; i < 18; i+=2)
			{
				Actor testFoodTemp = new Food(j,i);
				actors.add(testFoodTemp);
				nodes[j][i].children.add(testFoodTemp);
			}
		}
		actors.add(testHomeNW);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeNW);
		//homes.add((Home)testHomeNW);
		actors.add(testHomeNE);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeNE);
		//homes.add((Home)testHomeNE);
		actors.add(testHomeSW);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeSW);
		//homes.add((Home)testHomeSW);
		actors.add(testHomeSE);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeSE);
		//homes.add((Home)testHomeSE);
		//always last!?
		actors.add(testFox);
		nodes[testFox.getXY()[0]][testFox.getXY()[1]].children.add(testFox);
		
		
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
		calculateAveragePlantLocation();
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
				nodes[a.getXY()[0]][a.getXY()[1]].children.remove(a);
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
						nodes[b.getXY()[0]][b.getXY()[1]].children.remove(b);
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
			nodes[act.getXY()[0]][act.getXY()[1]].children.add(act);
			render();
			return true;
		}
		return false;
	}
	
	public boolean occupied(int x, int y)
	{
		
		
		/*for (Actor a : actors)
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
		}*/
		return !nodes[x][y].children.isEmpty();
		
	}
	
	public boolean occupiedExclusion(int x, int y,actorTYPE ex)
	{
		//for (Actor a : actors)
		//{
			//if (a.getXY()[0] == x && a.getXY()[1] == y && a != ex)
			if (!nodes[x][y].children.isEmpty())
			{
				for (Actor a : nodes[x][y].children)
				{
					if (a.getTYPE() != ex)
						return true;
				}
			}
		//}
		return false;
		
	}
	
	public Actor occupiedActorReturn(int x, int y, actorTYPE ex)
	{
		//System.out.println("checking occupied actor return for: " + x + " , " + y);
		if(!nodes[x][y].children.isEmpty())
		{
			//int i = 0;
			for (Actor a : nodes[x][y].children)
			{
				//System.out.println(a.getTYPE() + " " +i);
				if (a.getTYPE() == ex)
					return a;
			}
			//return nodes[x][y].children.get(0);
		}
		
		/*for (Actor a : actors)
		{
			if (a.getXY()[0] == x && a.getXY()[1] == y)
			{
				return a;
			}
			if (a.getTYPE() == actorTYPE.HOME)
			{
				for (Actor ayyLmao : ((Home)a).children)
				{
					if (ayyLmao.getXY()[0] == x && ayyLmao.getXY()[1] == y)
					{
						//System.out.println("found an actor");
						return ayyLmao;//can find actors
					}
				}
			}
		}*/
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
	
	public void setOverlay(int i)
	{
		overlay = i;
	}
	
	public void calculateAveragePlantLocation()
	{
		double avgX = 0;
		double avgY = 0;
		double count = 0;
		for (Actor a : actors)
		{
			if (a.getTYPE() == actorTYPE.FOOD)
			{
				avgX += a.getXY()[0];
				avgY += a.getXY()[1];
				count++;
			}
		}
		avgX /= count;
		avgY /= count;
		plant_avg_x = (int) avgX;
		plant_avg_y = (int) avgY;
		//System.out.println("Average plant location: " + "(" + plant_avg_x + " , " + plant_avg_y + ")");
	}
	
	
	
	
}

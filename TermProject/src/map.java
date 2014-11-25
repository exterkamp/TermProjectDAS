import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class map {
	
	
	int size;                        //size of the map in px
	BufferedImage mapObject = null;  //the image of the map
	BufferedImage BACKGROUND = null; //the grass background
	Graphics2D g2d;                  //the image's g2d
	Random rand = new Random();      //random number generator
	ArrayList<Actor> actors;         //the actor list
	int CELLSIZE;                    //the cell's size in px
	boolean WINNER = false;          //winner boolean flag
	int overlay;                     //0 is no overlay, 1 is path overlay
	int fight;                       //amount of fight
	int flight;                      //amount of flight
	int hunger;        				 //amount of hunger 
	int courage;					 //amount of courage
	int plant_avg_x, plant_avg_y;    //the average location of the remaining plants
	mapNode[][] nodes;               //the map object in node form
	
	
	

	public map(int sizeIn, int CELLSIZE, int[] stats)//400 = 25x25, 800 = 50x50
	{
		//set the overlay and size
		overlay = 0;
		this.CELLSIZE = CELLSIZE;
		//initialize the lists and such
		Random rand = new Random();
		actors = new ArrayList<Actor>();
		nodes = new mapNode[25][25];
		//basic average of the plants to start
		plant_avg_x = 12;
		plant_avg_y = 12;
		//initialize each node
		for (int row = 0;row < 25;row++)
		{
			for (int col = 0;col < 25;col++)
			{
				nodes[row][col] = new mapNode(row,col);
			}
		}
		//make the image have a 1 px border so it looks consistent
		size = sizeIn+1;
		//make the BACKGROUND and g2d for the BACKGROUND
		BACKGROUND = new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)BACKGROUND.getGraphics();
		//set stats
		fight = stats[0];
		flight = stats[1];
		hunger = stats[2];
		courage = stats[3];
		//set BACKGROUND as green
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
		//set the map and make the g2d for it
		mapObject = deepCopy(BACKGROUND);
		g2d = (Graphics2D)mapObject.getGraphics();
		g.dispose();

		//BASIC CODE FOR DEBUGGING
		//make the rabbit holes
		
		
		Actor testHomeNW = new Home(0,0,4);
		Actor testHomeNE = new Home(24,0,4);
		Actor testHomeSW = new Home(0,24,4);
		Actor testHomeSE = new Home(24,24,4);
		
		Actor testFoodContainer = new FoodContainer();
		//make food container!
		//make pattern of plants
				for (int j = 7;j < 18; j+=2)
				{
					for (int i = 7; i < 18; i+=2)
					{
						Food testFoodTemp = new Food(j,i);
						((FoodContainer)testFoodContainer).children.add(testFoodTemp);
						//nodes[j][i].children.add(testFoodTemp);
					}
				}
		
		//make fox
		Actor testFox = new Fox(12,15,7,18,13,18);
		Actor testFox2 = new Fox(12,9,7,18,7,12);
		//Actor testFox2 = new Fox(12,13);
		
		
		actors.add(testFoodContainer);
		((FoodContainer)testFoodContainer).init(this,nodes);
		//add homes and fox to actors list and the nodes
		actors.add(testHomeNW);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeNW);
		actors.add(testHomeNE);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeNE);
		actors.add(testHomeSW);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeSW);
		actors.add(testHomeSE);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeSE);
		actors.add(testFox);
		nodes[testFox.getXY()[0]][testFox.getXY()[1]].children.add(testFox);
		actors.add(testFox2);
		nodes[testFox2.getXY()[0]][testFox2.getXY()[1]].children.add(testFox2);
		//((Home)testHomeNW).active = false;
		//((Home)testHomeNE).active = false;
		//((Home)testHomeSW).active = false;
		render();
	}
	
	public BufferedImage getMap()
	{
		//return map
		return mapObject;
	}
	
	public void act()
	{
		//get the real average plant location
		calculateAveragePlantLocation();
		//flag for seeing if we have won
		boolean winner = true;
		//for (final java.util.Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); )
		for (Actor a : actors)
		{
			//Actor a = iterator.next();
			//have all actors act
			a.act(this, actors);
			//if the actor is FOOD or a BUNNY, since it may drop food, we have not won, so set winner to false
			if (a.getTYPE() == actorTYPE.CONTAINER)
			{
				//if there is any food then 
				if (!((FoodContainer)a).children.isEmpty())
					winner = false;
			}
		}
		ArrayList<Food> food2addBack = new ArrayList<Food>();
		//REMOVE DEAD ACTORS
		//iterate over all actors
		for (final java.util.Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); )
		{
			Actor a = iterator.next();
			//if it is dead remove it
			if (a.isDead())
			{
				nodes[a.getXY()[0]][a.getXY()[1]].children.remove(a);
				iterator.remove();
			}
			//if it is a HOME we need to recursivly check its children
			//HOMES never die, so the isDEAD will always be false, but so do foxes, so make sure the type is HOME
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
						if(b.full)
						{
							//queue up the addition of food items till after the spanning of the array
							
							//pass back the bunny's food object
							b.food.x = b.x;
							b.food.y = b.y;
							//food2addBack.add(b.food);
							b.food.edible = true;
							nodes[b.getXY()[0]][b.getXY()[1]].children.add(b.food);
							//food2addBack.add(new Point2D.Double(b.getXY()[0],b.getXY()[1]));
							
						}
						nodes[b.getXY()[0]][b.getXY()[1]].children.remove(b);
						//System.out.println("removed: " + b.toString());
						iteratorInner.remove();
					}
				}
				
			}
			else if(a.getTYPE() == actorTYPE.CONTAINER)
			{
				//delete old foods
				FoodContainer f = (FoodContainer)a;
				for (final Iterator<Food> iteratorInner = f.children.iterator(); iteratorInner.hasNext(); )
				{
					Food b = iteratorInner.next();
					if (b.isDead())
					{
						//System.out.println(occupied(b.getXY()[0],b.getXY()[1]));
						nodes[b.getXY()[0]][b.getXY()[1]].children.remove(b);
						//System.out.println(occupied(b.getXY()[0],b.getXY()[1]));
						//System.out.println("removed: " + b.toString());
						iteratorInner.remove();
					}
					else if (!b.edible)
					{
						if (this.occupiedActorReturn(b.getXY()[0],b.getXY()[1], actorTYPE.FOOD) != null)
							nodes[b.getXY()[0]][b.getXY()[1]].children.remove(b);
					}
				}
			}
			
		}
		//add any queued food
		//THIS PREVENTS COMODIFICATION
		
		
		//CHECK FOR WIN CONDITION
		if (!WINNER)
		{
			if (winner)//if we have no FOOD actors (we checked earlier)
			{
				int agg = 0;
				int murders = 0;
				//recall all bunnies from HOMES
				for (Actor a : actors)
				{
					//if it is a home
					if (a.getTYPE() == actorTYPE.HOME)
					{
						//recall the children
						((Home)a).recall();
						//aggregate the number of bunnies they made
						agg += ((Home)a).number_of_bunnies_spawned;
					}
					else if (a.getTYPE() == actorTYPE.FOX)
					{
						//if its a fox, see how many it murdered
						murders += ((Fox)a).murders;
					}
				}
				//WINNER is true!
				WINNER = true;
				System.out.println("Number of bunnies lost: " + murders);
				System.out.println("Number of bunnies used: " + agg);
				System.out.println("Mortality rate: " + (double)murders/(double)agg);
				
			}
		}
		//render!
		render();
	}
	
	public void render()
	{
		//deepcopy the BACKGROUND
		mapObject = deepCopy(BACKGROUND);
		//go through all the actors and render them
		for (Actor a : actors)
		{
			a.render(g2d, CELLSIZE);
		}
	}
	
	private BufferedImage deepCopy(BufferedImage bi) {
		//DEEPCOPY based on the ColorModel of the BACKGROUND
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 BufferedImage temp = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		 g2d = (Graphics2D)temp.getGraphics();
		 return temp;
	}
	
	public boolean addActor(int x, int y, actorTYPE a)
	{
		//how to add an actor to the game a la fences during runtime
		Actor act = null;
		switch (a)
		{
		case FENCE:
			act = new Fence(x,y);
			break;
			
		case FOOD:
			act = new Food(x,y);
			break;
		default:
			break;
		
			
		}
		//if we got a valid actor then add it to the list
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
		//check the node at x,y if it has children
		return !nodes[x][y].children.isEmpty();
		
	}
	
	public boolean occupiedExclusion(int x, int y,actorTYPE ex)
	{
		//if the node is not empty check it further
		if (!nodes[x][y].children.isEmpty())
		{
			for (Actor a : nodes[x][y].children)
			{
				//scan the actors in the node's children and if it anything other than the ex type, return true
				if (a.getTYPE() != ex)
					return true;
			}
		}
		return false;
		
	}
	
	public Actor occupiedActorReturn(int x, int y, actorTYPE ex)
	{
		//System.out.println("checking occupied actor return for: " + x + " , " + y);
		//if there is an actor
		if(!nodes[x][y].children.isEmpty())
		{
			//int i = 0;
			//scan through its children
			for (Actor a : nodes[x][y].children)
			{
				//System.out.println(a.getTYPE() + " " +i);
				//if it is of type ex then return it
				if (a.getTYPE() == ex)
					return a;
			}
			//return nodes[x][y].children.get(0);
		}
		return null;
		
	}
	public void changeStat(int num, int val)//1 = fight, 2 = flight, 3 = hunger, 4 = courage
	{
		//simple switch to change the state stats
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
		//simple getter
		int[] array = {fight,flight,hunger,courage};
		return array;
	}
	
	public void setOverlay(int i)
	{
		//setting the overlay
		overlay = i;
	}
	
	public void calculateAveragePlantLocation()
	{
		double avgX = 0;
		double avgY = 0;
		double count = 0;
		//scan all actors
		for (Actor a : actors)
		{
			//if it is a plant then add it to the totals
			if (a.getTYPE() == actorTYPE.FOOD)
			{
				//add its x's and y's tp the total
				avgX += a.getXY()[0];
				avgY += a.getXY()[1];
				count++;
			}
		}
		//divide by how many
		avgX /= count;
		avgY /= count;
		//modify
		plant_avg_x = (int) avgX;
		plant_avg_y = (int) avgY;
		//System.out.println("Average plant location: " + "(" + plant_avg_x + " , " + plant_avg_y + ")");
	}
	
	
	
	
}

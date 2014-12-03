import java.awt.Graphics2D;
//import java.awt.geom.Point2D;
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
	boolean GAME_OVER = false;       //game over to stop timer
	int overlay;                     //0 is no overlay, 1 is path overlay
	int fight;                       //amount of fight
	int flight;                      //amount of flight
	int hunger;        				 //amount of hunger 
	int courage;					 //amount of courage
	int plant_avg_x, plant_avg_y;    //the average location of the remaining plants
	mapNode[][] nodes;               //the map object in node form
	boolean difficulty_enabled;      //if the difficulty has been selected
	
	
	//statistics
	int active_bunnies_num = 0;
	int dead_bunnies_num   = 0;
	int bunnies_left_num   = 0;
	int max_bunnies        = 0;
	

	public map(int sizeIn, int CELLSIZE, int[] stats)//400 = 25x25, 800 = 50x50
	{
		
		difficulty_enabled = false;
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
		
		/*Actor testFoodContainer = new FoodContainer();
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
		//Actor testFox = new Fox(12,15,7,18,13,18);
		//Actor testFox2 = new Fox(12,9,7,18,7,12);
		Actor testFox = new Fox(12,15,6,19,12,19);
		Actor testFox2 = new Fox(12,9,6,19,6,13);
		//Actor testFox2 = new Fox(12,13);
		
		
		actors.add(testFoodContainer);
		((FoodContainer)testFoodContainer).init(this,nodes);*/
		//add homes and fox to actors list and the nodes
		actors.add(testHomeNW);
		nodes[testHomeNW.getXY()[0]][testHomeNW.getXY()[1]].children.add(testHomeNW);
		actors.add(testHomeNE);
		nodes[testHomeNE.getXY()[0]][testHomeNE.getXY()[1]].children.add(testHomeNE);
		actors.add(testHomeSW);
		nodes[testHomeSW.getXY()[0]][testHomeSW.getXY()[1]].children.add(testHomeSW);
		actors.add(testHomeSE);
		nodes[testHomeSE.getXY()[0]][testHomeSE.getXY()[1]].children.add(testHomeSE);
		/*actors.add(testFox);
		nodes[testFox.getXY()[0]][testFox.getXY()[1]].children.add(testFox);
		actors.add(testFox2);
		nodes[testFox2.getXY()[0]][testFox2.getXY()[1]].children.add(testFox2);*/
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
		boolean game_over = true;
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
		//ArrayList<Food> food2addBack = new ArrayList<Food>();
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
			//HOMES die when gameover only, so the isDEAD will always be false if bunnies outstanding, but so do foxes, so make sure the type is HOME
			else if(a.getTYPE() == actorTYPE.HOME)
			{
				//scan through home's stuff
				Home h = (Home)a;

				//garbage collect
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
				
				if (!h.active_complete)
					game_over = false;
				
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
		if (game_over)
			GAME_OVER = true;
		
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
		
		generateStastics();
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
			//System.out.println("placed at: " + x + "," + y);
			return true;
		}
		return false;
	}
	
	public boolean occupied(int x, int y)
	{
		//check the node at x,y if it has children
		//if (x > 24 || y > 24 || x < 0 || y < 0)
			return !nodes[x][y].children.isEmpty();
		//return false;
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
	
	public boolean occupiedNeighbor(int x, int y)
	{
		//if the node is not empty check it further
		if (!nodes[x][y].children.isEmpty())
		{
			for (Actor a : nodes[x][y].children)
			{
				//scan the actors in the node if it is something that should block, return true
				if (a.getTYPE() == actorTYPE.FOX || a.getTYPE() == actorTYPE.FENCE)
					return true;
			}
		}
		return false;
		
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
			if (a.getTYPE() == actorTYPE.CONTAINER)
			{
				for (Actor f : ((FoodContainer)a).children)
				{
					//add its x's and y's tp the total
					avgX += f.getXY()[0];
					avgY += f.getXY()[1];
					count++;
				}
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
	
	public void generateStastics()
	{
		//int agg = 0;
		int active = 0;
		int murders = 0;
		//recall all bunnies from HOMES
		for (Actor a : actors)
		{
			//if it is a home
			if (a.getTYPE() == actorTYPE.HOME)
			{
				//aggregate the number of bunnies they made
				//agg += ((Home)a).number_of_bunnies_spawned;
				active += ((Home)a).children.size();
			}
			else if (a.getTYPE() == actorTYPE.FOX)
			{
				//if its a fox, see how many it murdered
				murders += ((Fox)a).murders;
			}
		}		
		active_bunnies_num = active;
		dead_bunnies_num   = murders;
		bunnies_left_num   = max_bunnies - murders;
	}
	
	public void setEasy()
	{
		//
		Actor testFoodContainer = new FoodContainer();
		//make food container!
		//make pattern of plants
				for (int j = 7;j < 18; j+=3)
				{
					for (int i = 7; i < 18; i+=3)
					{
						Food testFoodTemp = new Food(j,i);
						((FoodContainer)testFoodContainer).children.add(testFoodTemp);
						//nodes[j][i].children.add(testFoodTemp);
					}
				}
		
		//make fox
		//Actor testFox = new Fox(12,15,7,18,13,18);
		//Actor testFox2 = new Fox(12,9,7,18,7,12);
		//Actor testFox = new Fox(12,15,6,19,12,19);
		Actor testFox2 = new Fox(12,12,6,19,6,19);
		//Actor testFox2 = new Fox(12,13);
		
		
		actors.add(testFoodContainer);
		((FoodContainer)testFoodContainer).init(this,nodes);
		//add homes and fox to actors list and the nodes
		//actors.add(testFox);
		//nodes[testFox.getXY()[0]][testFox.getXY()[1]].children.add(testFox);
		actors.add(testFox2);
		nodes[testFox2.getXY()[0]][testFox2.getXY()[1]].children.add(testFox2);
		
		//Add fences
		for(int x = 5; x < 8;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		for(int x = 17; x < 20;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		for(int y = 6; y < 8;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		for(int y = 17; y < 19;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		render();
		//System.out.println("setting up medium");
		max_bunnies = 10;
		bunnies_left_num = max_bunnies;
		difficulty_enabled = true;
	}
	
	public void setMedium()
	{
		//
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
		//Actor testFox = new Fox(12,15,7,18,13,18);
		//Actor testFox2 = new Fox(12,9,7,18,7,12);
		Actor testFox = new Fox(12,15,6,19,12,19);
		Actor testFox2 = new Fox(12,9,6,19,6,13);
		//Actor testFox2 = new Fox(12,13);
		
		
		actors.add(testFoodContainer);
		((FoodContainer)testFoodContainer).init(this,nodes);
		//add homes and fox to actors list and the nodes
		actors.add(testFox);
		nodes[testFox.getXY()[0]][testFox.getXY()[1]].children.add(testFox);
		actors.add(testFox2);
		nodes[testFox2.getXY()[0]][testFox2.getXY()[1]].children.add(testFox2);
		
		//Add fences
		for(int x = 5; x < 9;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		for(int x = 16; x < 20;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		for(int y = 6; y < 9;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		for(int y = 16; y < 19;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		for (int y = 11;y < 14;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		
		render();
		//System.out.println("setting up medium");
		max_bunnies = 75;
		bunnies_left_num = max_bunnies;
		difficulty_enabled = true;
	}
	
	public void setHard()
	{
		//
		//
		Actor testFoodContainer = new FoodContainer();
		//make food container!
		//make pattern of plants
				for (int j = 7;j < 18; j+=2)
				{
					for (int i = 7; i < 18; i+=1)
					{
						Food testFoodTemp = new Food(j,i);
						((FoodContainer)testFoodContainer).children.add(testFoodTemp);
						//nodes[j][i].children.add(testFoodTemp);
					}
				}
		
		//make fox
		//Actor testFox = new Fox(12,15,7,18,13,18);
		//Actor testFox2 = new Fox(12,9,7,18,7,12);
		Actor testFox = new Fox(12,15,6,19,12,19);
		Actor testFox2 = new Fox(12,9,6,19,6,13);
		//Actor testFox3 = new Fox(12,12,6,19,6,19);
		//Actor testFox2 = new Fox(12,13);
		
		
		actors.add(testFoodContainer);
		((FoodContainer)testFoodContainer).init(this,nodes);
		//add homes and fox to actors list and the nodes
		actors.add(testFox);
		nodes[testFox.getXY()[0]][testFox.getXY()[1]].children.add(testFox);
		actors.add(testFox2);
		nodes[testFox2.getXY()[0]][testFox2.getXY()[1]].children.add(testFox2);
		//actors.add(testFox3);
		//nodes[testFox3.getXY()[0]][testFox3.getXY()[1]].children.add(testFox3);
		//Add fences
		for(int x = 5; x < 9;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		for(int x = 16; x < 20;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		for(int y = 6; y < 9;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		for(int y = 16; y < 19;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		for (int y = 11;y < 14;y++)
		{
			this.addActor(5, y, actorTYPE.FENCE);
			this.addActor(19, y, actorTYPE.FENCE);
		}
		for (int x = 11;x < 14;x++)
		{
			this.addActor(x, 5, actorTYPE.FENCE);
			this.addActor(x, 19, actorTYPE.FENCE);
		}
		
		
		render();
		max_bunnies = 100;
		bunnies_left_num = max_bunnies;
		//System.out.println("setting up medium");
		difficulty_enabled = true;
	}
	
	public void cleanMap()
	{
		System.out.println("cleaning");
		System.out.println("number of actors in list: " + actors.size());
		int count = 0;
		for (int row = 0;row < 25;row++)
		{
			for (int col = 0;col < 25;col++)
			{
				if (nodes[row][col].children.size() != 0)
					count++;
			}
		}
		System.out.println("number of actors in nodes: " + count);
		
		for (final java.util.Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); )
		{
			Actor a = iterator.next();
			if (a.getTYPE() == actorTYPE.FENCE || a.getTYPE() == actorTYPE.FOX)
			{
				nodes[a.getXY()[0]][a.getXY()[1]].children.remove(a);
				iterator.remove();
			}
			if (a.getTYPE() == actorTYPE.CONTAINER)
			{
				for (Food f : ((FoodContainer)a).children)
				{
					nodes[f.getXY()[0]][f.getXY()[1]].children.remove(f);
					f.die();
				}
				iterator.remove();
			}
		}
		System.out.println("after clean:");
		System.out.println("number of actors in list: " + actors.size());
		count = 0;
		for (int row = 0;row < 25;row++)
		{
			for (int col = 0;col < 25;col++)
			{
				if (nodes[row][col].children.size() > 0 )
				{
					System.out.print(row + "," + col);
					count++;
				}
			}
		}
		System.out.println("\nnumber of actors in nodes: " + count);
		
	}
	
}

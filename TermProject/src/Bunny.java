import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


public class Bunny implements Actor {
	
	//VARS
	Random rand = new Random();         //the random object
	Astar stahr = new Astar();			//the astar object
	boolean dead = false;               //whether or not the bunny is dead or alive
	boolean full = false;				//if the bunny is full (has eaten a plant) or not
	Food food;							//the food object the bunny is carrying!
	
	int x;								//the x
	int y;								//the y
									//behavior statistics
									//maybe rename resourcefulness
	int fight; 						    //the more fight you have the more chance you will attack and distract the FOX
	int flight;						    //the more flight you have the more chance you will run away to a HIDE or HOME
	int hunger; 						//the more hunger you have the farther away you can see FOOD
	int courage;						//the more courage you have the more you will use DISTRACTIONS and not be scared
	double courage_confused_modifier;   //more courage, less chance of being scared randomly
	int scared_duration = 0;            //int of how many turns you will be scared
									//state enum
	public enum state {CONFUSED,SEEKING,EATING,ACTION,GOING_HOME,SCARED,NERVOUS};
	state currentState;			     	//the current state
	Stack<Point2D> path;				//the current path in a 2D stack
	boolean pathing;					//bool flag if you are currently pathing (or supposed to)
	Home home;							//the HOME you run away to (and came from)
	int overlay;						//overlay boolean
									//reference to the MAP
	boolean mapInit;
	map mappityMap;
	
	
	
	
	public Bunny(int xIn, int yIn, Home h, int fight, int flight, int hunger, int courage){
		//TODO - check if x,y are valid
		//set all variables that need to be set
		x = xIn;
		y = yIn;
		currentState = state.CONFUSED;
		path = null;
		pathing = false;
		home = h;
		this.fight = fight;
		this.flight = flight;
		this.hunger = hunger*2;//2-20 <- take #in a x2
		courage_confused_modifier =  0.1 - (((double)courage) / 100.0);
		overlay = 0;
		mapInit = false;
		mappityMap = null;
	}
	
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		//update the overlay to be current
		overlay = Map.overlay;
		if (!mapInit)
		{
			mappityMap = Map;
			mapInit = true;
		}
		//System.out.println(overlay + " <- rabbits, map -> " + Map.overlay);
		//make some temporary ints of the current X,Y
		int tempX = x, tempY = y;
		//MOVING
		switch (currentState)
		{
		case CONFUSED:
			
			pathing = false;
			path = null;
			//LOOK FOR SOME FOOD TO STOP BEING CONFUSED
			//hunger = 20;//2 is min, 25 max, 5 is meh, 10 is GOOD, 20 is BOOM BOOM
			//Point2D current = new Point2D.Double(x,y);
			Actor food = stahr.breadthFirstBubble(new Point2D.Double(this.x,this.y), actorTYPE.FOOD, (double)hunger, Map);
			if (food != null)
			{
				path = stahr.pathfindBreadthFirst(new Point2D.Double(this.x,this.y), new Point2D.Double(food.getXY()[0],food.getXY()[1]), Map);
				pathing = true;
				//System.out.println("Going to: " + path.toString() + " is " + food.toString());
				currentState = state.SEEKING;
			}
			
			
			/*
			ArrayList<Point2D> points_to_check = new ArrayList<Point2D>();
			for (int x = 0; x < 25; x++)
			{
				for (int y = 0; y < 25; y++)
				{
					//Point2D point = new Point2D.Double(x,y);
					if (Point2D.distance(this.x, this.y, x, y) <= hunger)
					{
						points_to_check.add(new Point2D.Double(x,y));
					}
				}
			}
			//search actors
			boolean found_path = false;
			for (Actor a : Map.actors)
			{
				Point2D aPoint = new Point2D.Double(a.getXY()[0], a.getXY()[1]);
				
				for (Point2D p : points_to_check)
				{
					if (aPoint.equals(p) && a.getTYPE() == actorTYPE.FOOD && !found_path)
					{
						Astar astar = new Astar();
						//change 0,0 to the bunny home in future code
						path = astar.pathfindBreadthFirst(new Point2D.Double(this.x,this.y), new Point2D.Double(aPoint.getX(),aPoint.getY()), Map);
						pathing = true;
						//System.out.println(path.toString());
						currentState = state.SEEKING;
						found_path = true;
					}
				}
			}
			*/

			
			double distance = Point2D.distance(x, y, Map.plant_avg_x, Map.plant_avg_y);
			//the larger the distance, the more likely we will move towards the middle
			//the max distance is from 12.5,12.5 to 4,4 or 21,21
			//max distance is = 12.0208
			//min is 0
			double percent_chance_of_random = distance / 30.0;
			//System.out.println(x+","+y);
			int deltX = rand.nextInt(3);
			deltX -= 1;
			int deltY = rand.nextInt(3);
			deltY -= 1;
			//instead of random, tend toward the average location of the plants!
			if (rand.nextDouble() > percent_chance_of_random)
			{
				
				x += deltX;
				y += deltY;
				if (x < 0 || x > 24)
				{
					x = tempX;
				}
				if (y < 0 || y > 24)
				{
					y = tempY;
				}
			}
			else
			{
				//move towards the middle
				//move towards the average of all the food location
				//path if behind a wall?
				//path to the average location/middle if probability is too high
				if (x > Map.plant_avg_x)
				{
					x--;
				}
				else
				{
					x++;
				}
				if(y > Map.plant_avg_y)
				{
					y--;
				}
				else
				{
					y++;
				}
				
			}
			
			break;
		case SEEKING:	
			if(pathing && path != null)
			{
				
				if (!path.isEmpty())
				{
					if (rand.nextDouble() > courage_confused_modifier)
					{
						Point2D p = path.pop();
						x = (int)p.getX();
						y = (int)p.getY();
						//System.out.println("seeking");
					}
					else
					{
						scared_duration = rand.nextInt(5);
						currentState = state.NERVOUS;
					}
				}
				else
				{
					currentState = state.CONFUSED;
					pathing = false;
					path = null;
					//System.out.println("seek over, going to confused");
				}
			}
			else
			{
				currentState = state.CONFUSED;
				path = null;
				//System.out.println("no pathing, switch to confused");
			}
			break;
		case NERVOUS:
			if(scared_duration > 0)
			{
				scared_duration--;
			}
			else
			{
				currentState = state.SEEKING;
			}
			break;
		case SCARED:
			//if scared_target != null
				//run away from it
			pathing = false;
			path = null;
			
			//scared_target == null
			if(scared_duration > 0)
			{
				deltX = rand.nextInt(3);
				deltX -= 1;
				deltY = rand.nextInt(3);
				deltY -= 1;
				x += deltX;
				y += deltY;
				if (x < 0 || x > 24)
				{
					x = tempX;
				}
				if (y < 0 || y > 24)
				{
					y = tempY;
				}
				scared_duration--;
			}
			else
			{
				currentState = state.CONFUSED;
			}
			break;
		case EATING:
			currentState = state.GOING_HOME;
			//System.out.println("eating");
			//reset all paths
			pathing = false;
			path = null;
			//re-act since eating doesn't take the whole turn
			act(Map,actors);
			break;
		case GOING_HOME://does not know to go home after getting scared by the fox
			//make path
			//System.out.println("going home!");
			if (!pathing && !(x == 0 && y == 0))//if not pathing and not home
			{
				//Astar a= new Astar();
				//change 0,0 to the bunny home in future code
				path = stahr.pathfindBreadthFirst(new Point2D.Double(x,y), new Point2D.Double(home.x,home.y), Map);
				if (path != null)
				{
					pathing = true;
				}
			}
			
			if(pathing)
			{
				
				if (!path.isEmpty())
				{
					Point2D p = path.pop();
					x = (int)p.getX();
					y = (int)p.getY();
					
				}
				else if(full == true)
				{
					//drop off food @ the home
					//this prevents the food from being REPLACED when it gets home
					full = false;
					//make food die as well?
					
					this.food.die();
					die();
				}
				else
				{
					die();
				}
			}
			break;
		default:
			break;
		}
		//CHECK FOR OVERLAP
		/*for (Actor a : actors)
		{
			int[] coor = a.getXY();
			if (this != a && x == coor[0] && y == coor[1])
			{
				//EAT PLANTS
				if (a.getTYPE() == actorTYPE.FOOD)
				{
					if (full == false)
					{
						//System.out.println("eating @ " + x + ". " + y);
						full = true;
						food = (Food)a;
						this.currentState = state.EATING;
						((Food)a).eat();
					}
				}
				//if (a.getTYPE() == actorTYPE.FOX)
				//{
				//	die();
				//	System.out.println("Bunny eaten!");
				//}
				else
				{
					//System.out.println("cancelling overlap");
					x = tempX;
					y = tempY;
				}
				
			}
			//MAKE SURE FOX ISN'T NEAR
			//THIS NEEDS WORK!
			if (a.getTYPE() == actorTYPE.FOX && currentState != state.SCARED)
			{
				double some_arbitrary_number = 4.0;
				double dist = Point2D.distance(x, y, a.getXY()[0], a.getXY()[1]);
				if (dist < some_arbitrary_number)
				{
					scared_duration = rand.nextInt(5);
					currentState = state.SCARED;
					pathing = false;
					path = null;
					//act(Map,actors);
				}
			}
			
		}*/
		if (Map.occupied(x, y))
		{
			//THIS SPACE IS OCCUPIED!
			//let's see with what!?
			Actor temp = null;
			if (Map.occupiedActorReturn(x, y, actorTYPE.FOOD) != null)
			{
				temp = Map.occupiedActorReturn(x, y, actorTYPE.FOOD);
				//FOOD!
				//eat it
				if (full == false)
				{
					//System.out.println("eating @ " + x + "," + y);
					full = true;
					food = (Food)temp;
					this.currentState = state.EATING;
					((Food)temp).eat();
					//Map.nodes[x][y].children.remove(temp);
				}
				
			}
			else if (Map.occupiedActorReturn(x, y, actorTYPE.FOX) != null)
			{
				//FOX!
				die();
			}
			else
			{
				//FENCE, or some other error
				//System.out.println("cancelling overlap");
				x = tempX;
				y = tempY;
			}
			
		}
		
		

		
		
		
		//adjust place in map nodes
		if (tempX != x || tempY != y)//if moved
		{
			Map.nodes[tempX][tempY].children.remove(this);//remove old
			Map.nodes[x][y].children.add(this);//add new
		}
		//System.out.println(Map.nodes[x][y].children.get(Map.nodes[x][y].children.indexOf(this)).getTYPE() + "@ point" + x + " , " + y);
		
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return dead;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		dead = true;
		//System.out.println("killing: " + this.toString());
		//mappityMap.addActor(x, y, actorTYPE.FOOD);
	}

	@Override
	public void render(Graphics2D g2d,int CELLSIZE) {
		// TODO Auto-generated method stub
		switch (currentState)
		{
		case SCARED:
			g2d.setColor(new Color(0xFFeaab88));//light red
			break;
		case NERVOUS:
			g2d.setColor(new Color(0xFFeae7b2));//light yellow
			break;
		case CONFUSED:
			g2d.setColor(new Color(0xFFccf4ca));//light green
			break;
		case GOING_HOME:
			g2d.setColor(new Color(0xFFdddddd));//light grey
			break;
		default:
			g2d.setColor(Color.WHITE);
			break;
		}
		
		int xReal = x * CELLSIZE;//CELLSIZE
		int yReal = y * CELLSIZE;//CELLSIZE
		g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
		
		//RENDER PATH
		
		if (path != null && path.size() > 0 && overlay == 1)
		{
			Color[] cols = new Color[path.size()];
			int red = 255;
			int alpha = 255;
			int inc = -1*(255/(path.size()));
			for (int i = cols.length-1; i >= 0; i--)
			{
				cols[i] = new Color(red,0,0,alpha);
				red += inc;
				alpha += inc;
			}
			inc = 0;
			Point2D old = null;
			for (Point2D p : path)
			{
				g2d.setColor(cols[inc]);//light grey
				inc++;
				g2d.fillRect(((int)p.getX() * CELLSIZE)+1+9, ((int)p.getY() * CELLSIZE)+1+9, CELLSIZE-1-16, CELLSIZE-1-16);
				if (old != null)
				g2d.drawLine(((int)old.getX() * CELLSIZE)+12, ((int)old.getY() * CELLSIZE)+12, ((int)p.getX() * CELLSIZE)+12, ((int)p.getY() * CELLSIZE)+12);
				old = p;
				
			}
		}
		
	}


	@Override
	public int[] getXY() {
		// TODO Auto-generated method stub
		int[] coor = {x,y};
		return coor;
	}


	@Override
	public actorTYPE getTYPE() {
		// TODO Auto-generated method stub
		return actorTYPE.BUNNY;
	}

}

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;


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
	int fight_duration = 0;
									//directionlal enum
	public enum direction {NW,N,NE,E,SE,S,SW,W};
	direction currentDirection;
	
									//state enum
	public enum state {CONFUSED,SEEKING,EATING,GOING_HOME,SCARED,NERVOUS,FIGHTING,FLIGHTING,RUNNING_AWAY};
	//confused is natural state, tends toward average plant position and searches for plants
	//seeking is when a plant is found and is pathing to it
	//eating is the action of eating a plant
	//going_home is, obvious, going to it's home object
	//scared is when it has seen the fox and will conduct an escape
	//nervous is when the path is interrupted by nervousness, stemming from a low COURAGE
	//fighting
	//flighting
	//running away 
	
	state currentState;			     	//the current state
	Stack<Point2D> path;				//the current path in a 2D stack
	boolean pathing;					//bool flag if you are currently pathing (or supposed to)
	Home home;							//the HOME you run away to (and came from)
	int overlay;						//overlay boolean
									//reference to the MAP
	boolean mapInit;
	map mappityMap;
	Actor fox;							//ref to fox if the bunny can see it
	
	
	
	public Bunny(int xIn, int yIn, Home h, int fight, int flight, int hunger, int courage){
		//TODO - check if x,y are valid
		//set all variables that need to be set
		x = xIn;
		y = yIn;
		currentState = state.CONFUSED;
		currentDirection = direction.N;
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
		fox = null;
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
				if (path.size() > 0 && Map.occupiedActorReturn((int)path.firstElement().getX(), (int)path.firstElement().getY(), actorTYPE.FOOD) == null)
				{
					//System.out.println("FOOD LOST! @ " + (int)path.firstElement().getX() + (int)path.firstElement().getY());
					currentState = state.CONFUSED;
					//pathing = false;
					//path = null;
				}
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
			pathing = false;
			path = null;
			//THREE OPTIONS
			//	RUN AWAY (REGULAR)
			//	RUN AWAY (AND DROP FOOD)
			//	RUN HOME & DROP FOOD
			//	FIGHT
			if (fox != null)
			{
				//seed the random decision by adding in all the chances
				int total = fight+flight+((fight+flight)/2);
				if (total == 0)
					total = 1;
				//fight and flight get their options 
				//and a regular escape gets an entry equal to the average of the two
				//total = total + (int)total/2;;
				
				int choice = rand.nextInt(total);
				if (choice < fight)
				{
					//FIGHT
					//STAY STILL AND SLOW DOWN FOX FOR rand(3) TURNS AFTER EATING RABBIT
					currentState = state.FIGHTING;
					//System.out.println("fighting");
					
				}
				else if (choice < (fight+flight))
				{
					//FLIGHT
					//RUN AWAY DOUBLE SPEED AND HOLD ONTO FOOD
					currentState = state.FLIGHTING;
					//System.out.println("flighting");
				}
				else
				{
					//RUN AWAY
					//RUN AWAY AND DROP FOOD AND RUN HOME IF COURAGE IS LOW
					//System.out.println("running away");
					if (rand.nextDouble() < courage_confused_modifier)
					{
						//courage is okay, run away
						currentState = state.RUNNING_AWAY;
					}
					else
					{
						//NO COURAGE
						//drop food and run home
						if(full)
						{
							//pass back the bunny's food object
							this.food.x = x;
							this.food.y = y;
							//food2addBack.add(b.food);
							this.food.edible = true;
							Map.nodes[x][y].children.add(this.food);
							this.food = null;
							this.full = false;
							//System.out.println("dropped food");
						}
						this.currentState = state.GOING_HOME;
						
					}
				}
				
				
				
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
			//pathing = false;
			//path = null;
			
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
		case RUNNING_AWAY:	
			//RUN AWAY REGULAR
			if (fox != null)
			{
				int xBAD = fox.getXY()[0];
				int yBAD = fox.getXY()[1];
				if (Point2D.distance(x, y, xBAD, yBAD) > 7)
				{
					//you've escaped!
					//GO HOME IF RECALLED
					if (full == false)
						currentState = state.CONFUSED;
					else
						currentState = state.GOING_HOME;
					
					fox = null;
				}
				else
				{
					if (xBAD > x)
					{
						x--;
					}
					else
					{
						x++;
					}
					if (yBAD > y)
					{
						y--;
					}
					else
					{
						y++;
					}
				}
			}
			else
			{
				currentState = state.CONFUSED;
			}
			break;
		case FLIGHTING:
			//RUN AWAY REGULAR
			if (fox != null)
			{
				int xBAD = fox.getXY()[0];
				int yBAD = fox.getXY()[1];
				if (Point2D.distance(x, y, xBAD, yBAD) > 7)
				{
					//you've escaped!
					//GO HOME IF RECALLED
					if (full == false)
						currentState = state.CONFUSED;
					else
						currentState = state.GOING_HOME;
					
					fox = null;
				}
				else
				{
					if (xBAD > x)
					{
						x--;
						x--;
					}
					else
					{
						x++;
						x++;
					}
					if (yBAD > y)
					{
						y--;
						y--;
					}
					else
					{
						y++;
						y++;
					}
				}
			}
			else
			{
				currentState = state.CONFUSED;
			}
			break;
		case FIGHTING:
			if (fight_duration >= 3)
			{
				fox = null;
				currentState = state.CONFUSED;
				fight_duration = 0;
			}
			fight_duration++;
			break;
		default:
			break;
		}
		
		/***********************************************************END OF STATE DEFININITIONS******************************************************/
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
		
		//CATCH OUT OF BOUNDS
		if (x <= 24 && x >= 0 && y <= 24 && y >= 0)
		{
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
		}
		else
		{
			x = tempX;
			y = tempY;
		}
		
		if (currentState != state.SCARED && fox == null)
		{
			for (Actor a : actors)
			{
				if (a.getTYPE() == actorTYPE.FOX)
				{
					if (((Fox)a).currentState != Fox.state.SLOWED)//if he is slowed, don't care
					{
						double some_arbitrary_number = 5.0;
						double dist = Point2D.distance(x, y, a.getXY()[0], a.getXY()[1]);
						if (dist < some_arbitrary_number)
						{
							scared_duration = rand.nextInt(5);
							currentState = state.SCARED;
							pathing = false;
							path = null;
							fox = a;
							//act(Map,actors);
							
						}
					}
				}
			}
		}
		
		//if the fox is slowed then don't be scared!
		if (fox != null)
		{
			if (((Fox)fox).currentState == Fox.state.SLOWED)
			{
				currentState = state.CONFUSED;
				fox = null;
				
			}
		}
		

		
		
		
		//adjust place in map nodes
		if (tempX != x || tempY != y)//if moved
		{
			Map.nodes[tempX][tempY].children.remove(this);//remove old
			Map.nodes[x][y].children.add(this);//add new
			
			//figure out the direction
			if (x > tempX)//EAST
			{
				if(y < tempY)//NORTH-EAST
				{
					currentDirection = direction.NE;
				}
				else if(y > tempY)//SOUTH-EAST
				{
					currentDirection = direction.SE;
				}
				else//EAST
				{
					currentDirection = direction.E;
				}
			}
			else if (x < tempX)//WEST
			{
				if(y < tempY)//NORTH-WEST
				{
					currentDirection = direction.NW;
				}
				else if (y > tempY)//SOUTH-WEST
				{
					currentDirection = direction.SW;
				}
				else
				{
					currentDirection = direction.W;
				}
			}
			else//NO X DIFF
			{
				if(y < tempY)//NORTH
				{
					currentDirection = direction.N;
				}
				else if (y > tempY)//SOUTH
				{
					currentDirection = direction.S;
				}
			}
			
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
			//if (flight > 1)
			//	g2d.setColor(new Color(0xFF000000));//light red	
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
		case FIGHTING:
			g2d.setColor(new Color(0xFF000000));//light grey
			break;
		case FLIGHTING:
			g2d.setColor(Color.YELLOW);//light grey
			break;
		case RUNNING_AWAY:
			g2d.setColor(Color.magenta);//light grey
			break;
		default:
			g2d.setColor(Color.WHITE);
			break;
		}
		
		int xReal = x * CELLSIZE;//CELLSIZE
		int yReal = y * CELLSIZE;//CELLSIZE
		//g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
		String filepath = "src/images/";
		String filename = "";
		BufferedImage img = null;
		switch (currentDirection)
		{
			case NW:
				filename = "NW";
				break;
			case N:
				filename = "N";
				break;
			case NE:
				filename = "NE";
				break;
			case E:
				filename = "E";
				break;
			case SE:
				filename = "SE";
				break;
			case S:
				filename = "S";
				break;
			case SW:
				filename = "SW";
				break;
			case W:
				filename = "W";
				break;
			default:
				filename = "N";
				break;
		}
		filepath = filepath + "" + filename;
		filepath = filepath + ".png";
		
		try {
		    img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
		}
		//g2d.drawImage(img, xReal, yReal, width, height, observer)
		//g2d.drawImage(img, BufferedImageOp.class, xReal, yReal);
		g2d.drawImage(img, xReal+1, yReal+1, xReal + CELLSIZE, yReal + CELLSIZE, 0, 0, 24, 24, null);
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

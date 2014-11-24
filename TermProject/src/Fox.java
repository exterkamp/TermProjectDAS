import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;



public class Fox implements Actor {
	
	//
	Random rand = new Random();
	int x; 
	int y;
	//state enum
	public enum state {LOOKING, HUNTING, GUARDING, EATING};
	//the current state
	state currentState;
	//the current path
	Stack<Point2D> path;
	boolean pathing;
	Actor target;
	Astar stahr = new Astar();
	//RESTRICTED COORDINATES
	//BASE- FOR NOW - WILL BE CUSTOMIZABLE
	/*int minX = 3;
	int maxX = 22;
	int minY = 3;
	int maxY = 22;*/
	int minX;// = 7;
	int maxX;// = 18;
	int minY;// = 7;
	int maxY;// = 18;
	//STATS
	int murders;
	
	public Fox(int x, int y,int minX,int maxX,int minY,int maxY) {
		this.x = x;
		this.y = y;
		currentState = state.LOOKING;
		path = null;
		pathing = false;
		target = null;
		murders = 0;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		
	}
	
	//LOOKING -> HUNTING -> GUARDING (if unsuccessful) -> REPEAT
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		int tempX = x, tempY = y;
		
		switch (currentState)
		{
		case LOOKING:
			//Actor a = stahr.breadthFirstBubble(new Point2D.Double(x,y), actorTYPE.BUNNY, 5.0, Map);
			Actor a = stahr.breadthFirstBubbleRestricted(new Point2D.Double(x,y),maxX,minX,maxY,minY, actorTYPE.BUNNY, 5.0, Map);
			//System.out.println(a);
			if (a == null)
			{
				//instead of pure random we want to TEND towards the middle until we get there and then TEND to be random
				double distance = Point2D.distance(x, y, 12.5, 12.5);
				//the larger the distance, the more likely we will move towards the middle
				//the max distance is from 12.5,12.5 to 4,4 or 21,21
				//max distance is = 12.0208
				//min is 0
				double percent_chance_of_random = distance / 12.0208;
				if (rand.nextDouble() > percent_chance_of_random)
				{
					//random walk, because close to middle
					int deltX = rand.nextInt(3);
					deltX -= 1;
					int deltY = rand.nextInt(3);
					deltY -= 1;
					x += deltX;
					y += deltY;
					
				}
				else
				{
					//move towards the middle
					//move towards the average of all the food location
					//path if behind a wall?
					//path to the average location/middle if probability is too high
					if (x > (maxX-minX))//middle of their patrol
					{
						x--;
					}
					else
					{
						x++;
					}
					if(y > (maxY-minY))
					{
						y--;
					}
					else
					{
						y++;
					}
					
				}
				
				if (x <= minX || x >= maxX)//they just walk past the bounds!
				{
					x = tempX;
				}
				if (y <= minY || y >= maxY)
				{
					y = tempY;
				}
				
			}
			else
			{
				//BUNNY FOUND!
				target = a;
				//System.out.println("begin hunting " + target.toString());
				//SET AN ATTENTION SPAN
				currentState = state.HUNTING;
			}
			break;
		case HUNTING:
			if(target != null)
			{
				path = stahr.pathfindBreadthFirst(new Point2D.Double(this.x,this.y), new Point2D.Double(target.getXY()[0],target.getXY()[1]), Map);
				pathing = true;
				
				if (path != null && !path.isEmpty())
				{
					if (path.size() > 1)
					{
						path.pop();
						if (path.size() > 1)
							path.pop();
					}
					Point2D p = path.pop();
					//System.out.println("position " + x + " , "+ y);
					if ((int)p.getX() > minX && (int)p.getX() < maxX && (int)p.getY() > minY && (int)p.getY() < maxY)
					{
						x = (int)p.getX();
						y = (int)p.getY();
					}
					else
					{
						//going back to "looking" causes flashing between HUNTING and LOOKING
						//System.out.println("looking because position outside max");
						currentState = state.LOOKING;
						pathing = false;
						path = null;
						target = null;
					}
					
					//System.out.println("new position " + x + " , "+ y);
					//System.out.println("seeking");
					//System.out.println(path.toString());
					if (target != null)
					{
						if (x == target.getXY()[0] && y == target.getXY()[1])
						{
							//System.out.println("eating bunny hunted: " + target.toString());
							murders++;
							target.die();
							target = null;
							currentState = state.LOOKING;
							pathing = false;
							path = null;
							//ADD A FOOD IF TARGET WAS FULL
							
						}
					}
				}
				else if (target == null)
				{
					//failure to path :(
					
					currentState = state.LOOKING;
					pathing = false;
					path = null;
					target = null;
					System.out.println("seek over, going to confused");
				}
			}
			else
			{
				currentState = state.LOOKING;
				path = null;
				pathing = false;
				//System.out.println("no pathing, switch to confused");
			}
			break;
		default:
			break;
		}
		
		//CHECK FOR OVERLAP
		for (Actor a : actors)
		{
			int[] coor = a.getXY();
			if (this != a && x == coor[0] && y == coor[1])
			{
				//EAT PLANTS
				if (a.getTYPE() == actorTYPE.BUNNY)
				{
					//System.out.println("eating bunny accident");
					a.die();
					murders++;
					target = null;
					currentState = state.LOOKING;
					//ADD A FOOD IF TARGET WAS FULL
					
				}
				else if (a.getTYPE() != actorTYPE.FOOD)
				{
					//System.out.println("cancelling overlap");
					x = tempX;
					y = tempY;
				}
						
			}
		}
		//adjust place in map nodes
		if (tempX != x || tempY != y)//if moved
		{
			Map.nodes[tempX][tempY].children.remove(this);//remove old
			Map.nodes[x][y].children.add(this);//add new
		}
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		//DOES NOT DIE
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;//ALWAYS
	}

	@Override
	public void render(Graphics2D g2d, int CELLSIZE) {
		// TODO Auto-generated method stub
		switch (currentState)
		{
		case EATING:
			g2d.setColor(new Color(0xFFdb8741));//light orange
			break;
		case HUNTING:
			g2d.setColor(new Color(0xFF9c1800));//deep red
			break;
		case GUARDING:
			g2d.setColor(new Color(0xFFdb978b));//washout orange
			break;
		default:
			g2d.setColor(new Color(0xFFe3482d));//orange
			break;
		}
		
		int xReal = x * CELLSIZE;//CELLSIZE
		int yReal = y * CELLSIZE;//CELLSIZE
		g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
		
		g2d.setColor(new Color(255,0,0,50));//orange
		g2d.fillRect((CELLSIZE*minX)+1, (CELLSIZE*minY)+1, (CELLSIZE*(maxX-minX))-1, (CELLSIZE*(maxY-minY))-1);
		
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
		return actorTYPE.FOX;
	}

}

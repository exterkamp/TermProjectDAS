import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


public class Bunny implements Actor {
	
	//VARS
	Random rand = new Random();
	//whether or not the bunny is dead or alive
	boolean dead = false;
	//if the bunny is full (has eaten a plant) or not
	boolean full = false;
	//coordinates
	int x;
	int y;
	//behavior statistics
	int fight;
	int fligt;
	int hunger;
	int courage;
	//state enum
	public enum state {CONFUSED,SEEKING,EATING,ESCAPING,FIGHTING,GOING_HOME};
	//the current state
	state currentState;
	//the current path
	Stack<Point2D> path;
	boolean pathing;
	
	public Bunny(int xIn, int yIn){
		//check if x,y are valid
		x = xIn;
		y = yIn;
		currentState = state.CONFUSED;
		path = null;
		pathing = false;
	}
	
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		
		int tempX = x, tempY = y;
		//MOVING
		switch (currentState)
		{
		case CONFUSED:
			//System.out.println(x+","+y);
			int deltX = rand.nextInt(3);
			deltX -= 1;
			int deltY = rand.nextInt(3);
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
			
			break;
			
		case GOING_HOME:
			//make path
			if (!pathing && !(x == 0 && y == 0))//if not pathing and not home
			{
				Astar a= new Astar();
				//change 0,0 to the bunny home in future code
				path = a.pathfindBreadthFirst(new Point2D.Double(x,y), new Point2D.Double(0,0), Map);
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
		for (Actor a : actors)
		{
			int[] coor = a.getXY();
			if (this != a && x == coor[0] && y == coor[1])
			{
				//EAT PLANTS
				if (a.isEdible())
				{
					//System.out.println("eating @ " + x + ". " + y);
					full = true;
					this.currentState = state.GOING_HOME;
					a.die();
				}
				else
				{
					//System.out.println("cancelling overlap");
					x = tempX;
					y = tempY;
				}
				
			}
		}
		
		
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
	}

	@Override
	public void render(Graphics2D g2d) {
		// TODO Auto-generated method stub
		g2d.setColor(Color.WHITE);
		int xReal = x * 24;//CELLSIZE
		int yReal = y * 24;//CELLSIZE
		g2d.fillRect(xReal+1, yReal+1, 23, 23);
	}


	@Override
	public int[] getXY() {
		// TODO Auto-generated method stub
		int[] coor = {x,y};
		return coor;
	}


	@Override
	public boolean isEdible() {
		// TODO Auto-generated method stub
		return false;
	}

}

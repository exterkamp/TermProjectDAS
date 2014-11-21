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
	
	public Fox(int x, int y) {
		this.x = x;
		this.y = y;
		currentState = state.LOOKING;
		path = null;
		pathing = false;
		target = null;
	}
	
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		int tempX = x, tempY = y;
		
		switch (currentState)
		{
		case LOOKING:
			Actor a = stahr.breadthFirstBubble(new Point2D.Double(x,y), actorTYPE.BUNNY, 5.0, Map);
			//System.out.println(a);
			if (a == null)
			{
				int deltX = rand.nextInt(3);
				deltX -= 1;
				int deltY = rand.nextInt(3);
				deltY -= 1;
				x += deltX;
				y += deltY;
				if (x < 3 || x > 21)
				{
					x = tempX;
				}
				if (y < 3 || y > 21)
				{
					y = tempY;
				}
			}
			else
			{
				//BUNNY FOUND!
				target = a;
				//path = stahr.pathfindBreadthFirst(new Point2D.Double(this.x,this.y), new Point2D.Double(a.getXY()[0],a.getXY()[1]), Map);
				//pathing = true;
				//System.out.println(target.toString());
				currentState = state.HUNTING;
			}
			break;
		case HUNTING:
			if(target != null)
			{
				path = stahr.pathfindBreadthFirst(new Point2D.Double(this.x,this.y), new Point2D.Double(target.getXY()[0],target.getXY()[1]), Map);
				pathing = true;
				if (!path.isEmpty())
				{
					if (path.size() > 1)
					{
						path.pop();
						if (path.size() > 1)
							path.pop();
					}
					Point2D p = path.pop();
					//System.out.println("position " + x + " , "+ y);
					x = (int)p.getX();
					y = (int)p.getY();
					//System.out.println("new position " + x + " , "+ y);
					//System.out.println("seeking");
					//System.out.println(path.toString());
					if (x == target.getXY()[0] && y == target.getXY()[1])
					{
						//System.out.println("eating bunny hunted");
						target.die();
						target = null;
						currentState = state.LOOKING;
					}
				}
				else
				{
					currentState = state.LOOKING;
					pathing = false;
					path = null;
					target = null;
					//System.out.println("seek over, going to confused");
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
					target = null;
					currentState = state.LOOKING;
					
				}
				else if (a.getTYPE() != actorTYPE.FOOD)
				{
					//System.out.println("cancelling overlap");
					x = tempX;
					y = tempY;
				}
						
			}
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

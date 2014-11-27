import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class tuple
{
	Point2D current;
	tuple from;
	
	public tuple(Point2D cur, tuple fro)
	{
		current = cur;
		from = fro;
	}
}

class CoordinateComparator implements Comparator<Point2D.Float> 
{
	public CoordinateComparator() {}

	 @Override
	 public int compare(Point2D.Float p1, Point2D.Float p2) {
	        if (p1.getX() < p2.getX()) 
	         return -1;
	        if (p1.getX() > p2.getX())
	         return 1;
	        if (p1.getY() < p2.getY())
	         return -1;
	        if (p1.getY() > p2.getY())
	         return 1;
	        return 0;
	 }
}

	//CoordinateComparator coordCompare = new CoordinateComparator();
	//TreeSet<Point2D.Float> coordSet = new TreeSet<Point2D.Float>(coordCompare);


public class Astar {
	
	
	public Astar(){
		//
	}
	
	//start = where the search originates from
	//a = what kind of thing you are searching for
	//range = the max range you want to look for
	//map = map reference
	public Actor breadthFirstBubble(Point2D start, actorTYPE a, double range, map Map)
	{
		ArrayList<Point2D> visited = new ArrayList<Point2D>();
		Queue<Point2D> frontier = new LinkedList<Point2D>();
		frontier.add(start);
		visited.add(start);
		while (!frontier.isEmpty())
		{
			Point2D current = frontier.poll();
			//visited.add(current);
			//System.out.println(frontier.size());
			
			Actor ayy = Map.occupiedActorReturn((int)current.getX(), (int)current.getY(),a);
			if (ayy != null)	
			{
				//System.out.println(ayy.getTYPE());
				//if (ayy.getTYPE() == a)
				//{
					//System.out.println("returning: " + ayy);
					return ayy;
				//}
			}
			
        	//for each neighbor in neighbor_nodes(current)
			ArrayList<Point2D> validNeighbors = getNeighborsExclusion(current,Map, a);
			
			for (Point2D neighbor : validNeighbors)
			{
				boolean contains = visited.contains(neighbor);
				if (Point2D.distance(start.getX(), start.getY(), neighbor.getX(), neighbor.getY()) <= range && !contains)
				{
					//System.out.println(Point2D.distance(start.getX(), start.getY(), neighbor.getX(), neighbor.getY()));
					frontier.add(neighbor);		
					visited.add(neighbor);
				}
			}
		}
		//System.out.println("returning null");
		return null;
	}
	
	public Actor breadthFirstBubbleRestricted(Point2D start,int maxX, int minX, int maxY, int minY, actorTYPE a, double range, map Map)
	{
		ArrayList<Point2D> visited = new ArrayList<Point2D>();
		Queue<Point2D> frontier = new LinkedList<Point2D>();
		frontier.add(start);
		visited.add(start);
		while (!frontier.isEmpty())
		{
			Point2D current = frontier.poll();
			//visited.add(current);
			//System.out.println(frontier.size());
			
			Actor ayy = Map.occupiedActorReturn((int)current.getX(), (int)current.getY(),a);
			if (ayy != null)	
			{
				//System.out.println(ayy.getTYPE());
				//if (ayy.getTYPE() == a)
				//{
					//System.out.println("returning: " + ayy);
					return ayy;
				//}
			}
			
        	//for each neighbor in neighbor_nodes(current)
			ArrayList<Point2D> validNeighbors = getNeighborsExclusionRestricted(current,Map, a,maxX,minX,maxY,minY);
			
			for (Point2D neighbor : validNeighbors)
			{
				boolean contains = visited.contains(neighbor);
				if (Point2D.distance(start.getX(), start.getY(), neighbor.getX(), neighbor.getY()) <= range && !contains)
				{
					//System.out.println(Point2D.distance(start.getX(), start.getY(), neighbor.getX(), neighbor.getY()));
					frontier.add(neighbor);		
					visited.add(neighbor);
				}
			}
		}
		//System.out.println("returning null");
		return null;
	}
	
	
	public Stack<Point2D> pathfindBreadthFirst(Point2D start, Point2D end, map m)
	{
		boolean DEBUG = false;
		if (DEBUG)
		System.out.println(start.toString() + " -> " + end.toString());
		//right now breadth first
        //openset := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
		Queue<Point2D> frontier = new LinkedList<Point2D>();
		
		frontier.add(start);
		//came_from := the empty map     //The map of navigated nodes.
		
		int[] came_from = new int[625];
		for (int i = 0; i < came_from.length;i++)
		{
			came_from[i] = -2;
		}
		//came_from.add(new tuple(start, null));
		came_from[(int)(start.getX() + start.getY()*25)] = -1;
		//while openset is not empty
		while (!frontier.isEmpty())
		{
			
        	//current := the node in openset having the lowest f_score[] value
			Point2D current = frontier.poll();
			
			if (DEBUG)
			System.out.println(current.toString());
        	//if current = goal
            	//return reconstruct_path(came_from, goal)
			if (current.getX() == end.getX() && current.getY() == end.getY())	
				return reconstructPath(came_from,start,end);
			
        	//for each neighbor in neighbor_nodes(current)
			//get neighbors, but exclude blocking for bunnies, which also do not block
			ArrayList<Point2D> validNeighbors = getNeighborsExclusion(current,m,actorTYPE.BUNNY);
			for (Point2D neighbor : validNeighbors)
			{
				boolean contains = false;
				
				if (came_from[(int)(neighbor.getX() + neighbor.getY()*25)] != -2)
				{
					contains = true;
					//if (DEBUG)
					//	System.out.println("contains duplicate");
				}
				
				if (!contains)
				{
					frontier.add(neighbor);
					//came_from.add(new tuple(neighbor, current));
					if (DEBUG)
					if ((int)(neighbor.getX() + neighbor.getY()*25) == 624)
					{
						System.out.println("where it came from: " + current.getX() + current.getY());
						System.out.println("index it came from: " + (int)(current.getX() + current.getY()*25));
					}
					came_from[(int)(neighbor.getX() + neighbor.getY()*25)] = (int)(current.getX() + current.getY()*25);
					
					
					if (DEBUG)
						System.out.println(neighbor.toString() + " came from " + current.toString());
				}
			}
		}
		//return failure
		System.out.print("FAILURE TO PATH FROM: ");
		System.out.println(start.toString() + " -> " + end.toString());
		
		//System.out.println(came_from[624]);
		//System.out.println(came_from[623]);
		//System.out.println(came_from[599]);
		//System.out.println(came_from[598]);
		//System.exit(0);
		return null;
	}
	
	public Stack<Point2D> pathfindBreadthFirst(Point2D start, int maxX, int minX, int maxY, int minY, Point2D end, map m)
	{
		boolean DEBUG = false;
		if (DEBUG)
		System.out.println(start.toString() + " -> " + end.toString());
		//right now breadth first
        //openset := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
		Queue<Point2D> frontier = new LinkedList<Point2D>();
		
		frontier.add(start);
		//came_from := the empty map     //The map of navigated nodes.
		
		int[] came_from = new int[625];
		for (int i = 0; i < came_from.length;i++)
		{
			came_from[i] = -2;
		}
		//came_from.add(new tuple(start, null));
		came_from[(int)(start.getX() + start.getY()*25)] = -1;
		//while openset is not empty
		while (!frontier.isEmpty())
		{
			
        	//current := the node in openset having the lowest f_score[] value
			Point2D current = frontier.poll();
			
			if (DEBUG)
			System.out.println(current.toString());
        	//if current = goal
            	//return reconstruct_path(came_from, goal)
			if (current.getX() == end.getX() && current.getY() == end.getY())	
				return reconstructPath(came_from,start,end);
			
        	//for each neighbor in neighbor_nodes(current)
			//get neighbors, but exclude blocking for bunnies, which also do not block
			//ArrayList<Point2D> validNeighbors = getNeighborsExclusion(current,m,actorTYPE.BUNNY);
			ArrayList<Point2D> validNeighbors = getNeighborsExclusionRestricted(current,m, actorTYPE.BUNNY,maxX,minX,maxY,minY);
			for (Point2D neighbor : validNeighbors)
			{
				boolean contains = false;
				
				if (came_from[(int)(neighbor.getX() + neighbor.getY()*25)] != -2)
				{
					contains = true;
					//if (DEBUG)
					//	System.out.println("contains duplicate");
				}
				
				if (!contains)
				{
					frontier.add(neighbor);
					//came_from.add(new tuple(neighbor, current));
					if (DEBUG)
					if ((int)(neighbor.getX() + neighbor.getY()*25) == 624)
					{
						System.out.println("where it came from: " + current.getX() + current.getY());
						System.out.println("index it came from: " + (int)(current.getX() + current.getY()*25));
					}
					came_from[(int)(neighbor.getX() + neighbor.getY()*25)] = (int)(current.getX() + current.getY()*25);
					
					
					if (DEBUG)
						System.out.println(neighbor.toString() + " came from " + current.toString());
				}
			}
		}
		//return failure
		System.out.print("FAILURE TO PATH FROM: ");
		System.out.println(start.toString() + " -> " + end.toString());
		System.out.println(" min: " + minX + "," + minY + " max: " + maxX + "," + maxY);
		//System.out.println(came_from[624]);
		//System.out.println(came_from[623]);
		//System.out.println(came_from[599]);
		//System.out.println(came_from[598]);
		//System.exit(0);
		return null;
	}
	
	
	
	public Stack<Point2D> reconstructPath(int[] came_from, Point2D start, Point2D end)
	{
		Point2D current = end;
		//function reconstruct_path(came_from,current)
    	//total_path := [current]
		//ArrayList<Point2D> path = new ArrayList<Point2D>();
		Stack<Point2D> pathInv = new Stack<Point2D>();
		pathInv.add(current);
    	//while current in came_from:
		while (current != start && came_from[(int)(current.getX() + current.getY()*25)] != -1)
		{
        	//current := came_from[current]
			
			int temp = came_from[(int)(current.getX() + current.getY()*25)];
			int x = temp % 25;
			int y = temp / 25;
			//if (temp == 624)
			//System.out.println(temp + " : " + x + " , " + y);
			//System.out.println(current.toString() + " came from " + new Point2D.Double(x,y).toString());
			current = new Point2D.Double(x,y);
			pathInv.add(current);
        	//total_path.append(current)
			
		}
    	//return total_path
		//System.out.println("Path: ");
		//while (!pathInv.isEmpty())
		//{
			//System.out.print(p.toString());
			//System.out.print(" -> ");
			//path.add(pathInv.pop());
		//}
		//System.out.println("Path: ");
		//for (Point2D p : pathInv)
		//{
		//	System.out.print(p.toString());
		//	System.out.print(" -> ");
		//}
		
		return pathInv;
	}
	
	public ArrayList<Point2D> getNeighbors(Point2D point, map m)
	{
		ArrayList<Point2D> neighbors = new ArrayList<Point2D>();
		for (double xX = point.getX()-1; xX <= point.getX()+1;xX++)
		{
			for (double yY = point.getY()-1; yY <= point.getY()+1;yY++)
			{
				if ((point.getX() != xX || point.getY() != yY) && xX >= 0 && xX < 25 && yY >= 0 && yY < 25)//and in bounds
				{
					neighbors.add(new Point2D.Double(xX,yY));
				}
			}
		}
		for (final Iterator<Point2D> iterator = neighbors.iterator(); iterator.hasNext(); )
		{
			Point2D p = iterator.next();
			if (m.occupiedExclusion((int)p.getX(), (int)p.getY(), actorTYPE.FOOD) && m.occupiedExclusion((int)p.getX(), (int)p.getY(), actorTYPE.HOME))
			{
				//remove
				//System.out.println("removing: " +(int)p.getX() + (int)p.getY());
				iterator.remove();
			}
		}
		return neighbors;
	}
	
	public ArrayList<Point2D> getNeighborsExclusion(Point2D point, map m,actorTYPE a)
	{
		ArrayList<Point2D> neighbors = new ArrayList<Point2D>();
		for (double xX = point.getX()-1; xX <= point.getX()+1;xX++)
		{
			for (double yY = point.getY()-1; yY <= point.getY()+1;yY++)
			{
				//if (xX == 24 && yY == 24)
				//System.out.println("checking: " + xX + "," + yY);
				if ((point.getX() != xX || point.getY() != yY) && xX >= 0 && xX < 25 && yY >= 0 && yY < 25)//and in bounds
				{
					neighbors.add(new Point2D.Double(xX,yY));
					//if (xX == 24 && yY == 24)
					//	System.out.println("adding: " + xX + "," + yY);
				}
			}
		}
		for (final Iterator<Point2D> iterator = neighbors.iterator(); iterator.hasNext(); )
		{
			Point2D p = iterator.next();
			//if (m.occupiedExclusion((int)p.getX(), (int)p.getY(), actorTYPE.FOOD) && m.occupiedExclusion((int)p.getX(), (int)p.getY(), actorTYPE.HOME) && m.occupiedExclusion((int)p.getX(), (int)p.getY(), a))
			if (m.occupiedNeighbor((int)p.getX(), (int)p.getY()))
			{
				//remove
				//System.out.println("removed " + p.toString() + "contains: " + m.nodes[(int)p.getX()][(int)p.getY()].children.toString());
				iterator.remove();
			}
		}
		return neighbors;
	}
	
	public ArrayList<Point2D> getNeighborsExclusionRestricted(Point2D point, map m,actorTYPE a, int maxX,int minX,int maxY,int minY)
	{
		ArrayList<Point2D> neighbors = new ArrayList<Point2D>();
		for (double xX = point.getX()-1; xX <= point.getX()+1;xX++)
		{
			for (double yY = point.getY()-1; yY <= point.getY()+1;yY++)
			{
				//if (xX == 24 && yY == 24)
				//System.out.println("checking: " + xX + "," + yY);
				if ((point.getX() != xX || point.getY() != yY) && xX >= minX && xX <= maxX && yY >= minY && yY <= maxY)//and in bounds
				{
					neighbors.add(new Point2D.Double(xX,yY));
					//if (xX == 24 && yY == 24)
					//	System.out.println("adding: " + xX + "," + yY);
				}
			}
		}
		for (final Iterator<Point2D> iterator = neighbors.iterator(); iterator.hasNext(); )
		{
			Point2D p = iterator.next();
			//if (m.occupiedExclusion((int)p.getX(), (int)p.getY(), actorTYPE.FOOD) && m.occupiedExclusion((int)p.getX(), (int)p.getY(), actorTYPE.HOME) && m.occupiedExclusion((int)p.getX(), (int)p.getY(), a))
			if (m.occupiedNeighbor((int)p.getX(), (int)p.getY()))
			{
				//remove
				//System.out.println("removed " + p.toString());
				iterator.remove();
			}
		}
		return neighbors;
	}
	
	
}

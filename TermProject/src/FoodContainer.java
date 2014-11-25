import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Map;



public class FoodContainer implements Actor{
	
	ArrayList<Food> children;
	
	public FoodContainer()
	{
		children = new ArrayList<Food>();
	}
	
	public void init(map m, mapNode[][] nodes)
	{
		for (Food f: children)
		{
			nodes[f.x][f.y].children.add(f);
		}		
	}
	
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		//do something?
		//System.out.println("Size: " + children.size());
		
		
		
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		//no functionality
		
		
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		//can't die
		return false;
	}

	@Override
	public void render(Graphics2D g2d, int CELLSIZE) {
		// TODO Auto-generated method stub
		//render's its children, namely, the food inside it
		for (Food f : children)
		{
			if (f.edible)
			{
				f.render(g2d, CELLSIZE);
			}
		}
		
	}

	@Override
	public int[] getXY() {
		// TODO Auto-generated method stub
		int[] n = {-1,-1};
		return n;
	}

	@Override
	public actorTYPE getTYPE() {
		// TODO Auto-generated method stub
		return actorTYPE.CONTAINER;
	}
	
	

}

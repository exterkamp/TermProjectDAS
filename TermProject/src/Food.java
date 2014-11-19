import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;


public class Food implements Actor {
	int x;
	int y;
	boolean edible;
	boolean dead;
	
	public Food(int xIn, int yIn){
		x = xIn;
		y = yIn;
		edible = true;
		dead = false;
	}
	
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		//I AM A PLANT!
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
		edible = false;
	}
	
	@Override
	public void render(Graphics2D g2d) {
		// TODO Auto-generated method stub
		if (isEdible())
		{
			g2d.setColor(Color.GREEN);
			int xReal = x * 24;//CELLSIZE
			int yReal = y * 24;//CELLSIZE
			g2d.fillRect(xReal+1, yReal+1, 23, 23);
		}
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
		return edible;
	}

}

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;


public class Fence implements Actor {
	
	int x;
	int y;
	
	public Fence(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		//null
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		//null, cannot kill
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		//always false
		return false;
	}

	@Override
	public void render(Graphics2D g2d) {
		// TODO Auto-generated method stub
		g2d.setColor(Color.GRAY);
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
	public actorTYPE getTYPE() {
		// TODO Auto-generated method stub
		return actorTYPE.FENCE;
	}

}

import java.awt.Graphics2D;
import java.util.ArrayList;


public interface Actor {
	public void act(map Map,ArrayList<Actor> actors);
	public void die();
	public boolean isDead();
	public void render(Graphics2D g2d);
	public int[] getXY();
	public boolean isEdible();
}

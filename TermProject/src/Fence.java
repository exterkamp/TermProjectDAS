//import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


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
	public void render(Graphics2D g2d, int CELLSIZE) {
		// TODO Auto-generated method stub
		//g2d.setColor(Color.GRAY);
		int xReal = x * CELLSIZE;//CELLSIZE
		int yReal = y * CELLSIZE;//CELLSIZE
		//g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
		
		String filepath = "src/images/FENCE.png";
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			
		}
		g2d.drawImage(img, xReal+1, yReal+1, xReal + CELLSIZE, yReal + CELLSIZE, 0, 0, 24, 24, null);
		
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

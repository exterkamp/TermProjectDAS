import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Food implements Actor {
	int x;
	int y;
	boolean edible;
	boolean dead;
	int owners;
	
	public Food(int xIn, int yIn){
		x = xIn;
		y = yIn;
		edible = true;
		dead = false;
		owners = 0;
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
		//edible = false;
	}
	
	@Override
	public void render(Graphics2D g2d,int CELLSIZE) {
		// TODO Auto-generated method stub
		if (edible)
		{
			/*g2d.setColor(Color.GREEN);
			int xReal = x * CELLSIZE;//CELLSIZE
			int yReal = y * CELLSIZE;//CELLSIZE
			g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);*/
			int xReal = x * CELLSIZE;//CELLSIZE
			int yReal = y * CELLSIZE;//CELLSIZE
			//g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
			String filepath = "src/images/";
			String filename = "FOOD";
			if (owners > 0)
			{
				filename = filename + "_DROPPED";
			}
			filepath = filepath + "" + filename;
			filepath = filepath + ".png";
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File(filepath));
			} catch (IOException e) {
			}
			//g2d.drawImage(img, xReal, yReal, width, height, observer)
			//g2d.drawImage(img, BufferedImageOp.class, xReal, yReal);
			g2d.drawImage(img, xReal+1, yReal+1, xReal + CELLSIZE, yReal + CELLSIZE, 0, 0, 24, 24, null);
			
			
		}
		else
		{
			g2d.setColor(new Color(0,0,255,100));
			int xReal = x * CELLSIZE;//CELLSIZE
			int yReal = y * CELLSIZE;//CELLSIZE
			g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
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
		return actorTYPE.FOOD;
	}
	
	public void eat(){
		edible = false;
	}

}

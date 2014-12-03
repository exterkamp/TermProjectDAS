import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;



public class Home implements Actor {
	int x;
	int y;
	int max;    //the max number of children
	boolean active;//if it will spawn bunnies or not
	boolean active_complete;//true if inactive AND no bunnies out
	int fight;  //the more fight you have the more chance you will attack and distract the FOX
	int fligt;  //the more flight you have the more chance you will run away to a HIDE or HOME
	int hunger; //the more hunger you have the farther away you can see FOOD
	int courage;//the more courage you have the more you will use DISTRACTIONS
	ArrayList<Bunny> children;
	long spawn_time_in_ms;//System.currentTimeMillis()
	long last_spawn_time_in_ms;
	
	//STATISTICS VARIABLES
	int number_of_bunnies_spawned;
	
	
	
	public Home(int x, int y,int max)
	{
		this.x = x;
		this.y = y;
		this.max = max;
		children = new ArrayList<Bunny>();
		active = true;
		active_complete = false;
		number_of_bunnies_spawned = 0;
		spawn_time_in_ms = 3000;//3000
		last_spawn_time_in_ms = 0;
	}
	
	
	@Override
	public void act(map Map, ArrayList<Actor> actors) {
		// TODO Auto-generated method stub
		if (!children.isEmpty() || active)
		{
			//ACT ALL CHILDREN
			for (final Iterator<Bunny> iterator = children.iterator(); iterator.hasNext(); )
			{
				Bunny a = iterator.next();
				//if (a.isDead())
				//{
					//this code literally never happens...
				//	System.out.println("removing in home");
					
				//	iterator.remove();
				//}
				//else
				//{
					if (!active)
						a.currentState = Bunny.state.GOING_HOME;
					
					a.act(Map, actors);
				//}
			}
			//SPAWN A BUNNY RANDOMLY OR WHATNOT
			long now = System.currentTimeMillis();
			//if (children.size() == 0){}
			
			if (now - last_spawn_time_in_ms > spawn_time_in_ms)
			{
				if(!Map.occupiedExclusion(x, y,actorTYPE.HOME) && children.size() < max && active)
				{
					int[] stats = Map.getStats();
					Actor bunny = new Bunny(x,y,this,stats[0],stats[1],stats[2],stats[3]);
					children.add((Bunny)bunny);
					Map.nodes[x][y].children.add(bunny);//add new
					number_of_bunnies_spawned++;
					last_spawn_time_in_ms = now;
				}
			}
			//System.out.println("# children: " + children.size());
		}
		else
		{
			//set some flag
			//System.out.println("I am complete!" + this.toString());
			active_complete = true;
			//System.out.println("amount of children right now: " + children.size());
			//this.die();
		}
		
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		//DOES NOT DIE
		//dead = true;
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;//always
	}

	@Override
	public void render(Graphics2D g2d,int CELLSIZE) {
		// TODO Auto-generated method stub
		for (Bunny b : children)
		{
			b.render(g2d,CELLSIZE);
		}
		//
		int xReal = x * CELLSIZE;//CELLSIZE
		int yReal = y * CELLSIZE;//CELLSIZE
		//g2d.fillRect(xReal+1, yReal+1, CELLSIZE-1, CELLSIZE-1);
		
		String filepath = "src/images/HOME.png";
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			
		}
		g2d.drawImage(img, xReal+1, yReal+1, xReal + CELLSIZE, yReal + CELLSIZE, 0, 0, 24, 24, null);
		
		
		
		long now = System.currentTimeMillis();
		//if (children.size() == 0){}
		int time = (int)(now - last_spawn_time_in_ms) / 1000;
		//System.out.println("actual long: " + (now - last_spawn_time_in_ms));
		//System.out.println(time);
		//time is in ASCII 
		char c = (char) ((spawn_time_in_ms/1000 - time) + 48);
		char[] chars = {c};
		//g2d.setColor(Color.WHITE);
		if (active)
			g2d.setColor(Color.WHITE);          //black -> active home
		else
			g2d.setColor(new Color(0xFF651616));//not active, RED tinted black home
		g2d.drawChars(chars, 0, 1, xReal+CELLSIZE/2-4, yReal+CELLSIZE/2);
		//System.out.println(children.toString());
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
		return actorTYPE.HOME;
	}
	
	public void recall()
	{
		active = false;
		for (Bunny b : children)
		{
			//RECALL EVERYTHING BACK TO HOME @ END OF GAME
			b.currentState = Bunny.state.GOING_HOME;
			//make them drop any food
			//b.full = false;
			
			b.pathing = false;
			b.path = null;
			
		}
	}
	
}

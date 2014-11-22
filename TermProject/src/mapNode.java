import java.util.ArrayList;


public class mapNode {
	ArrayList<Actor> children;
	int x;
	int y;
	
	public mapNode(int x, int y){
		children = new ArrayList<Actor>();
		this.x = x;
		this.y = y;
	}
	
}

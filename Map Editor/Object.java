import java.awt.*;

public class Object{

	private int x, y, width, height;

	public Object(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Object(int x, int y){
		this(x,y,1,1);
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void changeX(int dx){
		x += dx;
	}

	public void changeY(int dy){
		y += dy;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public int getLeft(){
		return getX()-getWidth()/2;
	}

	public int getTop(){
		return getY()-getHeight()/2;
	}

	public void paint(Graphics g){
		g.setColor(Color.white);
	}
}
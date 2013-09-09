import java.awt.*;

public class SolidMaker extends Object{

	private int endx, endy;

	public SolidMaker(int x, int y){
		super(x,y);
		endx = x;
		endy = y;
	}

	public void setEnd(int x, int y){
		endx = x;
		endy = y;
	}

	public int getWidth(){
		return endx-getX();
	}

	public int getHeight(){
		return endy-getY();
	}

	public void paint(Graphics g){
		g.setColor(Color.gray);
		g.drawRect(getX(),getY(),getWidth(),getHeight());
		g.setColor(Color.white);
		g.drawLine(getX(),getY()-3,getX(),getY());
		g.drawLine(getX()-3,getY(),getX(),getY());
	}
}
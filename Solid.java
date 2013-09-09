import java.awt.*;

public class Solid extends Object{
	
	public Solid(int x, int y, int width, int height){
		super(x,y,width,height);
	}

	public void paint(Graphics g){
		g.setColor(new Color(180,180,200));
		g.drawRect(getLeft(),getTop(),getWidth(),getHeight());
	}
}
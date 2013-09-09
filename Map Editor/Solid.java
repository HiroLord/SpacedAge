import java.awt.*;

public class Solid extends Object{
	public Solid(int x, int y, int width, int height){
		super(x,y,width,height);
	}

	public void paint(Graphics g){
		g.setColor(new Color(1,1,1,.2f));
		g.fillRect(getLeft(),getTop(),getWidth(),getHeight());
		g.setColor(Color.white);
		g.drawRect(getLeft(),getTop(),getWidth(),getHeight());
	}
}
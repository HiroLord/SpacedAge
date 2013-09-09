import java.awt.*;

public class Spawn extends Object{

	private int team;

	public Spawn(int x, int y, int team){
		super(x,y,32,32);
		this.team = team;
	}

	public int getTeam(){
		return team;
	}

	public void paint(Graphics g){
		g.setColor(new Color(10,255,10));
		if (team == 1)
			g.setColor(new Color(255,10,10));
		else if (team == 2)
			g.setColor(new Color(10,10,255));
		g.drawRect(getLeft()+1,getTop()+1,30,30);
		g.drawRect(getLeft(),getTop(),32,32);
	}
}
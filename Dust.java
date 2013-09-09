import java.awt.*;
import java.util.Random;

public class Dust extends Object{

	private double dir1, dir2, dir3;
	private int loc1x, loc1y;
	private int loc2x, loc2y;
	private int loc3x, loc3y;
	private int life;

	public Dust(int x, int y){
		super(x,y,1,1);
		Random rand = new Random();
		dir1 = (rand.nextInt(200)/100.0)*Math.PI;
		dir2 = (rand.nextInt(200)/100.0)*Math.PI;
		dir3 = (rand.nextInt(200)/100.0)*Math.PI;
		loc1x = (int)lengthdir_x(4,dir1);
		loc2x = (int)lengthdir_x(4,dir2);
		loc3x = (int)lengthdir_x(4,dir3);
		loc1y = (int)lengthdir_y(4,dir1);
		loc2y = (int)lengthdir_y(4,dir2);
		loc3y = (int)lengthdir_y(4,dir3);
		life = 12;
	}

	public void setMove(){
		life -= 1;
	}

	public int getLife(){
		return life;
	}

	public void paint(Graphics g){
		g.setColor(new Color(160,160,160));
		g.drawRect(getX()+loc1x,getY()+loc1y,3,3);
		g.drawRect(getX()+loc2x,getY()+loc2y,3,3);
		g.drawRect(getX()+loc3x,getY()+loc3y,3,3);
	}
}
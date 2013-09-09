import java.awt.*;

public class Bullet extends Object{

	private double dir;
	private int owner;
	private int speed = 20;
	private int life;
	private int power;

	public Bullet(int x, int y, double dir, int owner, int power){
		super(x,y,12,12);
		this.dir = dir;
		this.owner = owner;
		this.power = power;
		life = 400;
	}

	public int getOwner(){
		return owner;
	}

	public int getPower(){
		return power;
	}

	public int getLife(){
		return life;
	}

	public void setMove(){
		life -= 1;
		changeX(lengthdir_x(speed,dir));
		changeY(lengthdir_y(speed,dir));
	}

	public void paint(Graphics g){
		g.setColor(Color.orange);
		drawAngleRect(g,getX(),getY(),dir,8,6);
	}
}
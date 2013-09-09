import java.awt.*;

public class Object{
	private double x, y;
	private double xx, yy;
	private double dx, dy;
	private int width, height;

	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;

	public static final double GRAVITY = Controller.GRAVITY;

	public Object(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setMove(){
	}

	public int getLife(){
		return 1;
	}

	public int getX(){
		return (int)x;
	}

	public int getY(){
		return (int)y;
	}

	public void setX(int x){
		this.xx = x;
		this.x = x;
	}

	public void setY(int y){
		this.yy = y;
		this.y = y;
	}

	public void changeX(double x){
		this.xx += x;
		this.x += x;
	}

	public void changeY(double y){
		this.yy += y;
		this.y += y;
	}

	public void changeXX(int dxx){
		xx += dxx;
	}

	public void changeYY(int dyy){
		yy += dyy;
	}

	public void setXX(int xx){
		this.xx = xx;
	}

	public void setYY(int yy){
		this.yy = yy;
	}

	public int getXX(){
		return (int)xx;
	}

	public int getYY(){
		return (int)yy;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public double getdx(){
		return dx;
	}

	public double getdy(){
		return dy;
	}

	public void setdx(double dx){
		this.dx = dx;
	}

	public void setdy(double dy){
		this.dy = dy;
	}

	public void changedx(double dx){
		this.dx += dx;
	}

	public void changedy(double dy){
		this.dy += dy;
	}

	public int getLeft(){
		return (int)x - (int)width/2;
	}

	public int getTop(){
		return (int)y - (int)height/2;
	}

	public int getRight(){
		return (int)x + (int)width/2;
	}

	public int getBottom(){
		return (int)y + (int)height/2;
	}

	public Point coordinates(){
		return new Point(getX(),getY());
	}

	public void drawAngleRect(Graphics g, int stx, int sty, int endx, int endy, int width){
		Point stPoint = new Point(stx,sty);
		Point endPoint = new Point(endx,endy);
		double dir = pointDirection(stPoint,endPoint);

		Point a = new Point(stx+(int)lengthdir_x(width,dir+Math.PI*3/4),sty+(int)lengthdir_y(width,dir+Math.PI*3/4));
		Point b = new Point(stx+(int)lengthdir_x(width,dir-Math.PI*3/4),sty+(int)lengthdir_y(width,dir-Math.PI*3/4));
		Point d = new Point(endx+(int)lengthdir_x(width,dir+Math.PI/4),endy+(int)lengthdir_y(width,dir+Math.PI/4));
		Point c = new Point(endx+(int)lengthdir_x(width,dir-Math.PI/4),endy+(int)lengthdir_y(width,dir-Math.PI/4));
		drawLine(g,a,b);
		drawLine(g,b,c);
		drawLine(g,c,d);
		drawLine(g,d,a);
	}

	public void drawAngleRect(Graphics g, int stx, int sty, double dir, int length, int width){
		int endx = stx + (int)lengthdir_x(length,dir);
		int endy = sty + (int)lengthdir_y(length,dir);
		drawAngleRect(g,stx,sty,endx,endy,width);
	}

	public void drawLine(Graphics g, Point a, Point b){
		g.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
	}

	public double lengthdir_x(int speed, double dir){
		return Math.cos(dir)*speed;
	}

	public double lengthdir_y(int speed, double dir){
		return -Math.sin(dir)*speed;
	}

	public double pointDistance(Point a, Point b){
		int xchange = Math.abs((int)(a.getX()-b.getX()));
		int ychange = Math.abs((int)(a.getY()-b.getY()));
		double output = Math.sqrt((xchange*xchange)+(ychange*ychange));
		return output;
	}

	public double pointDirection(Point a, Point b){
		int xchange = (int)(b.getX()-a.getX());
		int ychange = (int)(b.getY()-a.getY());
		double dir;
		if (xchange == 0){
			if (ychange >= 0)
				dir = -Math.PI/2;
			else
				dir = Math.PI/2;
		}
		else{
			dir = Math.atan2(xchange,ychange)-Math.PI/2;
		}
		return dir;
	}

	public void paint(Graphics g){
//		g.setColor(new Color(200,200,200));
//		g.drawRect(getLeft(),getTop(),getWidth(),getHeight());
	}
}
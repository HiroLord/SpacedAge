import java.awt.*;
import java.awt.event.*; 
import javax.swing.Timer;

public class Player extends Object{

	private boolean left = false, right = false, up = false, down = false;
	private int olddir;
	private boolean jump = false;
	private int maxSpeed = 4;
	private double acceleration = 0.35;
	private double friction = 1.7;
	private int jumpHeight = 9;
	private boolean firing = false;
	private int team;

	private int fdir = 0;

	private boolean changed = false, sendDir = false;;

	private int recoil = 0;
	private int hp, maxhp;

	private int score, kills, deaths;
	private boolean dead = false;
	private int respawn = 0;

	private int ID;

	private Weapon[] weapon;

	private Timer dirChangeTimer;

	public double nmdir, mdir, oldDir;

	public Player(int x, int y, int id){
		super(x,y,32,32);
		this.ID = id;
		team = 0;
		weapon = new Weapon[2];
		weapon[0] = new Weapon(1);

		maxhp = 100;
		hp = maxhp;

		nmdir = 0;
		mdir = nmdir;

		score = 0;
		kills = 0;
		deaths = 0;

		dirChangeTimer = new Timer(333,new DirChanger());
		dirChangeTimer.start();
	}

	private class DirChanger implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (oldDir != mdir)
				sendDir = true;
			oldDir = mdir;
		}
	}

	public void setRespawn(int r){
		respawn = r;
	}

	public int getRespawn(){
		return respawn;
	}

	public void addKill(){
		kills += 1;
	}

	public void addDeath(){
		deaths += 1;
	}

	public void setDead(boolean d){
		dead = d;
	}

	public boolean getDead(){
		return dead;
	}

	public void addScore(int s){
		score += s;
	}

	public void setScore(int s){
		score = s;
	}

	public void setKills(int k){
		kills = k;
	}

	public void setDeaths(int d){
		deaths = d;
	}

	public boolean getSendDir(){
		return sendDir;
	}

	public void setSendDir(boolean sd){
		sendDir = sd;
	}

	public int getHP(){
		return hp;
	}

	public int getMaxHP(){
		return maxhp;
	}

	public void changeHP(int dL){
		hp += dL;
	}

	public void setHP(int l){
		hp = l;
	}

	public void setID(int id){
		ID = id;
	}

	public int getID(){
		return ID;
	}

	public int getFDir(){
		return fdir;
	}

	public void setmdir(double mdir){
		this.mdir = mdir;
		this.nmdir = mdir;
	}

	public void setnmdir(double nmdir){
		this.nmdir = nmdir;
	}

	public boolean getChanged(){
		return changed;
	}

	public void parseMovement(){
		if (nmdir != mdir){
			if (Math.abs(mdir-nmdir)<Math.PI){
				if (mdir < nmdir)
					mdir += Math.PI/30;
				else
					mdir -= Math.PI/30;
			}
			else{
				if (mdir < nmdir)
					mdir -= Math.PI/30;
				else
					mdir += Math.PI/30;
			}
			if (mdir < -Math.PI*3/2)
				mdir += Math.PI*2;
			if (mdir > Math.PI/2)
				mdir -= Math.PI*2;
			if (Math.abs(nmdir-mdir) < (Math.PI/30))
				mdir = nmdir;
		}
		changed = false;

		if ((left && right) || dead){
			left = false;
			right = false;
		}

		if (left)
			fdir = LEFT;
		else if (right)
			fdir = RIGHT;
		else
			fdir = 0;

		if (recoil > 0)
			recoil -= 1;

		if (olddir != fdir){
			changed = true;
		}
		if (dead){
			changed = false;
			firing = false;
		}
		olddir = fdir;
	}

	public void setMove(){
		if (dead && respawn > 0)
			respawn -= 1;
		/*
		if (getX() < getXX()){
			changeXX(-1);
			changeX(1);
		}
		else if (getX() > getXX()){
			changeXX(1);
			changeX(-1);
		}
		if (getY() < getYY()){
			changeYY(-1);
			changeY(1);
		}
		else if (getY() > getYY()){
			changeYY(1);
			changeY(-1);
		}
		*/

		parseMovement();

		changedy(GRAVITY);
		if (left){
			fdir = LEFT;
			if (getdx() > -maxSpeed)
				changedx(-acceleration);
		}
		else{
			if (getdx() < 0)
				changedx(acceleration*friction);
		}

		if (right){
			fdir = RIGHT;
			if (getdx() < maxSpeed)
				changedx(acceleration);
		}
		else{
			if (getdx() > 0)
				changedx(-acceleration*friction);
		}

		if (!left && !right){
			fdir = 0;
			if (Math.abs(getdx()) < acceleration*friction){
				setdx(0);
			}
		}

		if (down)
			changedy(2);

		if (getdx() < -maxSpeed)
			setdx(-maxSpeed);
		else if (getdx() > maxSpeed)
			setdx(maxSpeed);

		if (jump){
			setdy(-jumpHeight);
			jump = false;
		}
		else if (getdy() > 12)
			setdy(12);

		/*if (up)
			setdy(-2);
		else if (down)
			setdy(2);
		else
			setdy(0);*/
	}

	public void setLeft(boolean l){
		left = l;
	}

	public void setRight(boolean r){
		right = r;
	}

	public void setUp(boolean u){
		up = u;
	}

	public void setDown(boolean d){
		down = d;
	}

	public void setJump(boolean j){
		jump = j;
	}

	public boolean getJump(){
		return jump;
	}

	public int getJumpHeight(){
		return jumpHeight;
	}

	public boolean getFiring(){
		return firing;
	}

	public void setFiring(boolean fire){
		firing = fire;
	}

	public void setWeapon(int index, Weapon weapon){
		this.weapon[index-1] = weapon;
	}

	public Weapon getWeapon(){
		return weapon[0];
	}

	public int getRecoil(){
		return recoil;
	}

	public boolean getleft(){
		return left;
	}

	public boolean getright(){
		return right;
	}

	public boolean getup(){
		return up;
	}

	public boolean getdown(){
		return down;
	}

	public void setTeam(int team){
		this.team = team;
	}

	public int getTeam(){
		return team;
	}

	public void setRecoil(int recoil){
		this.recoil = recoil;
	}

	public void paint(Graphics g){
		g.setColor(new Color(50,50,50));
		g.fillRect(getLeft()+1,getTop()+1,30,30);
		if (!dead)
			g.setColor(new Color(10,255,10));
		else
			g.setColor(new Color(10,140,10));
		if (team == 1){
			if (!dead)
				g.setColor(new Color(255,10,10));
			else
				g.setColor(new Color(140,10,10));
		}
		else if (team == 2){
			if (!dead)
				g.setColor(new Color(10,10,255));
			else
				g.setColor(new Color(10,10,140));
		}
		g.drawRect(getLeft()+2,getTop()+2,28,28);
		g.drawRect(getLeft()+1,getTop()+1,30,30);
		g.setColor(new Color(255,245,245));
		if (!dead)
			g.drawString(kills+"/"+deaths,getLeft()+3,getTop()-2);
		else if (respawn > 0)
			g.drawString(String.format("Respawn: %.2f",respawn/60.0),getLeft(),getTop()-2);
		g.setColor(new Color(170,190,170));
		if (!dead)
			drawAngleRect(g,getX(),getY(),mdir,18,6);
	}
}
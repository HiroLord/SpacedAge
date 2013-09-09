import java.util.ArrayList;
import java.awt.*;

public class Game{

	private int mode;
	private String name;
	private String map;
	private ArrayList<int[]> spawns;
	private int[] scores;
	private int maxScore;
	private boolean hasTeams;
	private int respawnTime;

	public Game(String map, int mode){
		this.map = map;
		this.mode = mode;
		hasTeams = false;
		maxScore = 10;
		if (mode == 1)
			name = "Deathmatch";
		if (mode == 2){
			name = "Team Deathmatch";
			maxScore = 50;
		}
		if (mode == 3)
			name = "CTF";
		if (mode > 1)
			hasTeams = true;
		scores = new int[3];
		scores[0] = 0;
		scores[1] = 0;
		scores[2] = 0;
		spawns = new ArrayList<int[]>();
	}

	public void addSpawn(int[] spawn){
		spawns.add(spawn);
	}

	public boolean hasTeams(){
		return hasTeams;
	}

	public void setRespawnTime(int rt){
		respawnTime = rt;
	}

	public int getRespawnTime(){
		return respawnTime;
	}

	public void setScore(int i, int s){
		scores[i] = s;
	}

	public int getScore(int i){
		return scores[i];
	}

	public ArrayList<int[]> getSpawns(){
		return spawns;
	}

	public int getMode(){
		return mode;
	}

	public String getMapName(){
		return map;
	}

	public String getName(){
		return name;
	}

	public Color getTeamColor(int team){
		if (team == 1)
			return Color.red;
		if (team == 2)
			return Color.blue;
		return Color.green;
	}

	public void fillSlantRect(Graphics g, int stx, int sty, int width, int height, int slope){
		int ch = 1;
		if (width < 0)
			ch = -1;
		for (int i = 0; i < Math.abs(width); i++){
			g.drawLine(stx,sty,stx+height*slope,sty+height);
			stx += ch;
		}
	}

	public void paint(Graphics g, int team){
		g.setColor(Color.red.darker());
		//fillSlantRect(g,320+24,0,128,24,-1);
		g.setColor(Color.blue.darker());
		Controller.fillSlantRect(g,320+32,0,(int)(119*((scores[2]*1.0)/maxScore))+1,24,-1);
		g.setColor(Color.red.darker());
		Controller.fillSlantRect(g,320-32,0,(-1)*((int)(119*((scores[1]*1.0)/maxScore))+1),24,1);

		g.setColor(getTeamColor(team));
		g.fillOval(320-32,-32,64,64);
		g.setColor(Color.white);
		g.drawOval(320-32,-32,64,64);
		g.drawOval(320-24,-24,48,48);
		g.setColor(Color.RED);
		g.drawLine(320-128-24,0,320-128,24);
		g.drawLine(320-128,24,320-22,24);
		g.setColor(Color.BLUE);
		g.drawLine(320+128+24,0,320+128,24);
		g.drawLine(320+128,24,320+22,24);
	}
}
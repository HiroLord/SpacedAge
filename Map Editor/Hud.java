import java.util.ArrayList;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class Hud extends Object{

	private int team;
	private ArrayList<TileSet> tilesets;
	private int tilesetnum;

	public Hud(){
		super(0,0);
		tilesets = new ArrayList<TileSet>();
		tilesetnum = 0;
		team = 0;
	}

	public void changeTeam(){
		team += 1;
		if (team > 2){
			team = 0;
		}
	}

	public int getTeam(){
		return team;
	}

	public void addTileSet(TileSet ts){
		tilesets.add(ts);
	}

	public TileSet getTileSet(){
		return tilesets.get(tilesetnum);
	}

	public void paint(Graphics g){
		TileSet ts = tilesets.get(tilesetnum);
		BufferedImage bI = new BufferedImage(160,192,BufferedImage.TYPE_INT_RGB);
		Graphics bIg = bI.getGraphics();
		int num = ts.getLastNum();
		int tilesize = ts.getTileSize();
		int width = ts.getSet().getWidth();
		int height = ts.getSet().getHeight();
		int i = 0;
		int st_x = 0;
		int st_y = 0;
		while (i < num){
			st_x += tilesize;
			if (st_x >= width){
				st_x = 0;
				st_y += tilesize;
			}
			i += 1;
		}
		int dx = 0;
		int dy = 0;
		while (st_x > 160){
			st_x -= tilesize;
			dx -= tilesize;
		}
		while (st_y > 192){
			st_y -= tilesize;
			dy -= tilesize;
		}
		bIg.drawImage(ts.getSet(),dx,dy,null);
		g.drawImage(bI,16,16,null);
		g.setColor(Color.white);
		g.drawRect(15,15,161,193);
		g.setColor(Color.red);
		g.drawRect(16+st_x,16+st_y,tilesize,tilesize);

		g.setColor(Color.black);
		String teamname = "Neutral";
		if (team == 1)
			teamname = "Red";
		else if (team == 2)
			teamname = "Blue";
		g.drawString("Editing team "+teamname,16,230);
	}
}
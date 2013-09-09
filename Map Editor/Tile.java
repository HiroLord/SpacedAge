import java.util.ArrayList;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class Tile extends Object{

	TileSet tileset;
	int tilenum;
	BufferedImage tile;

	public Tile(int x, int y, TileSet tileset, int tilenum){
		super(x,y);
		this.tileset = tileset;
		this.tilenum = tilenum;
		grabTile();
	}

	public TileSet getTileset(){
		return tileset;
	}

	public int getTile(){
		return tilenum;
	}

	public void changeTile(int ch){
		tilenum += ch;
		if (tilenum < 0)
			tilenum = tileset.getSize()-1;
		else if (tilenum > tileset.getSize()-1)
			tilenum = 0;
		grabTile();
	}

	public void grabTile(){
		tile = tileset.getTile(tilenum);
	}

	public void paint(Graphics g){
		g.drawImage(tile,getX(),getY(),null);
	}
}
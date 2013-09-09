import java.util.ArrayList;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class TileSet{

	private String source;
	private BufferedImage set;
	private int tilesize;
	private ArrayList<BufferedImage> tiles;
	private int lastNum;

	public TileSet(String fname, int tilesize){
		source = fname;
		tiles = new ArrayList<BufferedImage>();
		this.tilesize = tilesize;
		System.out.println("Generating tileset.");
		generateTileset();
		System.out.println("Tileset generated.");
	}

	public void generateTileset(){
		try{
			set = ImageIO.read(new File(source));
			if (set.getHeight()%tilesize != 0 || set.getWidth()%tilesize!= 0){
				System.out.println("Incorrect tilesize.\nTileset Generation halted.");
			}
			else{
				for (int y = 0; y < set.getHeight(); y+=tilesize){
					for (int x = 0; x < set.getWidth(); x+=tilesize){
						tiles.add(getTile(x,y));
					}
				}
			}
		} catch (IOException i){
			System.err.println("Unable to generate map.");
			i.printStackTrace();
		}
	}

	public BufferedImage getTile(int num){
		lastNum = num;
		return tiles.get(num);
	}

	public int getLastNum(){
		return lastNum;
	}

	public BufferedImage getSet(){
		return set;
	}

	public int getSize(){
		return tiles.size();
	}

	public int getTileSize(){
		return tilesize;
	}

	public BufferedImage getTile(int x, int y){
	    BufferedImage image = new BufferedImage(tilesize,tilesize,BufferedImage.TYPE_INT_RGB);
	    Graphics g = image.getGraphics();
	    g.drawImage(set,-x,-y,null);
	    return image;
	}
}
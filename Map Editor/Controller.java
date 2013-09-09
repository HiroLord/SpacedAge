import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.event.*; 
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;


public class Controller extends JComponent implements ActionListener{

	private JFrame frame;
	private Timer runner;

	private Color BOUNDARY_COLOR = new Color(100,100,100);
	private Color BG_COLOR = new Color(0,0,0);
	private BufferedImage backgroundImage;
	private int WIDTH = 800;
	private int HEIGHT = 480;
	private BufferedImage BGIMAGE;
	private int gridSize = 16;

	private int windowHeight;
	private int windowWidth;

	private int mouse_x, mouse_y;

	private Tile placingTile;
	private Object erasingTile;
	private Object solidTiler;
	private SolidMaker solidMaker;
	private Object spawnTiler;
	private int mode;
	private int modes = 4;

	private boolean shift = false;
	private boolean snap = true;
	private int setdrx, setdry;

	private Hud hud;

	private ArrayList<Object> objects;
	private ArrayList<Tile> tiles;
	private ArrayList<Object> visibleObjects;
	private ArrayList<Solid> solids;
	private ArrayList<Spawn> spawns;

	private Object origin;

	public Controller(JFrame frame, int mapWidth, int mapHeight, String mapName){
		this.frame = frame;
		WIDTH = mapWidth;
		HEIGHT = mapHeight;
		runner = new Timer(15,this);
		setFocusable(true);
		addKeyListener(new KeyInput());
		addMouseListener(new MouseInput());

		origin = new Object(0,0);

		tiles = new ArrayList<Tile>();
		visibleObjects = new ArrayList<Object>();
		objects = new ArrayList<Object>();
		solids = new ArrayList<Solid>();
		spawns = new ArrayList<Spawn>();

		objects.add(origin);

		mode = 1;

		hud = new Hud();
		hud.addTileSet(new TileSet("tileset.bmp",16));
		visibleObjects.add(hud);

		if (!mapName.equals("")){
			loadMap(mapName);
		}

		setMode(mode);

		for (Object o : objects){
			o.changeX(192+16);
			o.changeY(16);
		}

		runner.start();
	}

	public void setMode(int mode){
		deleteObject(erasingTile);
		deleteObject(placingTile);
		deleteObject(solidMaker);
		deleteObject(solidTiler);
		deleteObject(spawnTiler);
		if (mode == 1){
			placingTile = new Tile(64,64,hud.getTileSet(),0);
			visibleObjects.add(placingTile);
		}
		else if (mode == 2){
			erasingTile = new Object(64,64);
			visibleObjects.add(erasingTile);
		}
		else if (mode == 3){
			solidTiler = new Object(64,64);
			visibleObjects.add(solidTiler);
		}
		else if (mode == 4){
			spawnTiler = new Object(64,64);
			visibleObjects.add(spawnTiler);
		}
	}

	public void bringToFront(Object o){
		visibleObjects.remove(o);
		visibleObjects.add(o);
	}

	public BufferedImage generateMap() throws IOException{
		int chX = origin.getX();
		int chY = origin.getY();
		for (Tile tt : tiles){
			tt.changeX(-chX);
			tt.changeY(-chY);
		}
		for (Solid ss : solids){
			ss.changeX(-chX);
			ss.changeY(-chY);
		}
		for (Spawn sp : spawns){
			sp.changeX(-chX);
			sp.changeY(-chY);
		}

	    BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	    Graphics mg = image.getGraphics();
	    mg.setColor(BG_COLOR);
		mg.fillRect(0,0,WIDTH,HEIGHT);
		if (backgroundImage != null){
			mg.drawImage(backgroundImage,0,0,null);
		}
	    for (Tile t : tiles){
	    	t.paint(mg);
		}

		DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("newmap.map")));
		//BufferedOutputStream out = new BufferedOutputStream(fstream);
		for (Solid s : solids){
			out.writeByte(1);
			out.writeShort(s.getX());
			out.writeShort(s.getY());
			out.writeShort(s.getWidth());
			out.writeShort(s.getHeight());
		}
		for (Spawn sp : spawns){
			out.writeShort(2);
			out.writeShort(sp.getTeam());
			out.writeShort(sp.getX());
			out.writeShort(sp.getY());
		}
		System.out.println("Map data generation...");
		out.close();

  		for (Solid ss : solids){
			ss.changeX(chX);
			ss.changeY(chY);
		}
		for (Tile tt : tiles){
			tt.changeX(chX);
			tt.changeY(chY);
		}
		for (Spawn sp : spawns){
			sp.changeX(chX);
			sp.changeY(chY);
		}
	    return image;
	}

	public void loadMap(String map){
		System.out.println("Loading map: "+map+".map");
		try{
			BGIMAGE = ImageIO.read(new File(map+".png"));
			WIDTH = BGIMAGE.getWidth();
			HEIGHT = BGIMAGE.getHeight();
			System.out.println("Map image loaded.");
		} catch (IOException e){
			System.out.println("Could not load map image.");
		}

		try{
			System.out.println("Loading map data.");
			InputStream f = new FileInputStream(new File(map+".map"));
			DataInputStream in = new DataInputStream(f);
			while (in.available() > 0){
				int type = in.readByte();
				switch (type){
					case 1:
					int crx = in.readShort();
					int cry = in.readShort();
					int crw = in.readShort();
					int crh = in.readShort();
					addSolid(new Solid(crx,cry,crw,crh));
					break;
					case 2:
					int[] spawn = new int[3];
					spawn[0] = in.readShort();
					spawn[1] = in.readShort();
					spawn[2] = in.readShort();
					Spawn newSpawn = new Spawn(spawn[1],spawn[2],spawn[0]);
					spawns.add(newSpawn);
					visibleObjects.add(newSpawn);
					objects.add(newSpawn);
					break;
				}
			}
			in.close();
			System.out.println("Loaded map data.");
		} catch (Exception e){
			System.err.println("Could not load map data.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void addSolid(Solid solid){
		visibleObjects.add(solid);
		objects.add(solid);
		solids.add(solid);
	}

	private class KeyInput implements KeyListener{
		public void keyPressed(KeyEvent e){
			switch (e.getKeyCode()){
				case KeyEvent.VK_LEFT:
				for (Object o : objects)
					o.changeX(16);
				break;

				case KeyEvent.VK_RIGHT:
				for (Object o : objects)
					o.changeX(-16);
				break;

				case KeyEvent.VK_UP:
				for (Object o : objects)
					o.changeY(16);
				break;

				case KeyEvent.VK_DOWN:
				for (Object o : objects)
					o.changeY(-16);
				break;

				case KeyEvent.VK_ESCAPE:
				deleteObject(solidMaker);
				solidMaker = null;
				break;

				case KeyEvent.VK_W:
				if (mode == 1)
					placingTile.changeTile(1);
				break;

				case KeyEvent.VK_Q:
				if (mode == 1)
					placingTile.changeTile(-1);
				break;

				case KeyEvent.VK_T:
				hud.changeTeam();
				break;

				case KeyEvent.VK_S:
				if (snap == true)
					snap = false;
				else
					snap = true;
				break;

				case KeyEvent.VK_E:
				mode += 1;
				if (mode > modes)
					mode = 1;
				setMode(mode);
				break;

				case KeyEvent.VK_R:
				int mx = 192+16-origin.getX();
				int my = 16 - origin.getY();
				for (Object o : objects){
					o.changeX(mx);
					o.changeY(my);
				}
				break;

				case KeyEvent.VK_SHIFT:
				shift = true;
				break;
			}
		}
		public void keyReleased(KeyEvent e){
			switch (e.getKeyCode()){
				case KeyEvent.VK_SPACE:
				System.out.println("Generating map...");
				try{
					boolean[] okay = new boolean[2];
					okay[0] = false;
					okay[1] = false;
					for (Spawn s : spawns){
						if (s.getTeam() == 0){
							okay[0] = true;
							okay[1] = true;
						}
						else if (s.getTeam() == 1)
							okay[0] = true;
						else if (s.getTeam() == 2)
							okay[1] = true;
					}
					if (okay[0] && okay[1] && solids.size() > 0 && spawns.size() > 0){
						BufferedImage map = generateMap();
						System.out.println("Saving image...");
						ImageIO.write(map,"png",new File("newmap.png"));
						System.out.println("Saved map!");
					}
					else{
						System.out.println("Missing items.");
					}
				} catch (IOException i){
					i.printStackTrace();
					System.out.println("Failed to save.");
					System.exit(1);
				}
				break;

				case KeyEvent.VK_SHIFT:
				shift = false;
				break;
			}
		}
		public void keyTyped(KeyEvent e){

		}
	}

	private class MouseInput implements MouseListener{
		public void mousePressed(MouseEvent e){
			if (mouse_x < 192){

			}
			else{
				switch(mode){
					case 1:
					addTile(new Tile(placingTile.getX(),placingTile.getY(),placingTile.getTileset(),placingTile.getTile()));
					bringToFront(placingTile);
					break;
					case 2:
					Object rt = null;
					for (Tile t : tiles){
						if (t.getX() == erasingTile.getX() && t.getY() == erasingTile.getY()){
							rt = t;
							break;
						}
					}
					for (Solid s : solids){
						if (checkCollision(s,erasingTile)){
							rt = s;
							break;
						}
					}
					for (Spawn sp : spawns){
						if (checkCollision(sp,erasingTile)){
							rt = sp;
							break;
						}
					}
					if (rt != null)
						deleteObject(rt);
					break;
					case 3:
					if (solidMaker == null){
						solidMaker = new SolidMaker(solidTiler.getX(),solidTiler.getY());
						visibleObjects.add(solidMaker);
						objects.add(solidMaker);
					}
					else{
						Solid s = null;
						if (solidMaker.getWidth() > 0 && solidMaker.getHeight() > 0)
							s = new Solid(solidMaker.getX()+solidMaker.getWidth()/2,solidMaker.getY()+solidMaker.getHeight()/2,solidMaker.getWidth(),solidMaker.getHeight());
						boolean toAdd = true;
						for (Solid cs : solids)
							if (checkCollision(s,cs))
								toAdd = false;
						if (toAdd == true)
							addSolid(s);
						else
							s = null;
						deleteObject(solidMaker);
						solidMaker = null;
					}
					break;
					case 4:
					Spawn sp = new Spawn(spawnTiler.getX()+16,spawnTiler.getY()+16,hud.getTeam());
					boolean toAdd = true;
					for (Solid s : solids){
						if (checkCollision(s,sp)){
							toAdd = false;
							break;
						}
					}
					if (sp.getX() <= origin.getX()+16 || sp.getY() <= origin.getY()+16 || sp.getX() >= origin.getX()+WIDTH-16 || sp.getY() >= origin.getY()+HEIGHT-16){
						toAdd = false;
					}
					if (toAdd){
						visibleObjects.add(sp);
						objects.add(sp);
						spawns.add(sp);
					}
					break;
				}
			}
		}
		public void mouseReleased(MouseEvent e){

		}
		public void mouseEntered(MouseEvent e){

		}
		public void mouseExited(MouseEvent e){

		}
		public void mouseClicked(MouseEvent e){

		}
	}

	public void addTile(Tile t){
		tiles.add(t);
		visibleObjects.add(0,t);
		objects.add(t);
	}

	public void deleteObject(Object o){
		if (o != null){
			tiles.remove(o);
			objects.remove(o);
			visibleObjects.remove(o);
			solids.remove(o);
			spawns.remove(o);
			o = null;
		}
	}

	public void update(){
		setdrx = mouse_x;
		setdry = mouse_y;
		if (snap){
			setdrx -= mouse_x%gridSize;
			setdry -= mouse_y%gridSize;
		}

		if (mode == 1){
			placingTile.setX(setdrx);
			placingTile.setY(setdry);
		}
		else if (mode == 2){
			erasingTile.setX(setdrx-8);
			erasingTile.setY(setdry-8);
		}
		else if (mode == 3){
			solidTiler.setX(setdrx);
			solidTiler.setY(setdry);
			if (solidMaker != null)
				solidMaker.setEnd(solidTiler.getX(),solidTiler.getY());
		}
		else if (mode == 4){
			spawnTiler.setX(setdrx);
			spawnTiler.setY(setdry);
		}
	}

	public void actionPerformed(ActionEvent e){
		windowWidth  = frame.getContentPane().getWidth();
		windowHeight = frame.getContentPane().getHeight();

		PointerInfo mo = MouseInfo.getPointerInfo();
		Point m = mo.getLocation();
		mouse_x = (int) m.getX() - frame.getX() - (frame.getWidth() - windowWidth)/2;
		mouse_y = (int) m.getY() - frame.getY() - (frame.getHeight() - windowHeight);

		update();
		repaint();
	}

	public boolean checkCollision(Object a, Object b){
		int xdist = Math.abs((int)(b.getX() - a.getX()));
		int ydist = Math.abs((int)(b.getY() - a.getY()));
		if ((xdist < a.getWidth()/2+b.getWidth()/2) && (ydist < a.getHeight()/2+b.getHeight()/2))
			return true;
		return false;
	}

	public void drawMouse(Graphics g, int mouse_x, int mouse_y, boolean type){
		if (type){
			g.drawLine(mouse_x,mouse_y,mouse_x,mouse_y+14);
			g.drawLine(mouse_x,mouse_y,mouse_x+10,mouse_y+10);
			g.drawLine(mouse_x,mouse_y+14,mouse_x+4,mouse_y+10);
			g.drawLine(mouse_x+10,mouse_y+10,mouse_x+4,mouse_y+10);
		}
		else{
			g.drawLine(mouse_x-5,mouse_y,mouse_x+5,mouse_y);
			g.drawLine(mouse_x,mouse_y-5,mouse_x,mouse_y+5);
			g.drawLine(mouse_x-9,mouse_y-5,mouse_x-9,mouse_y+5);
			g.drawLine(mouse_x-9,mouse_y-5,mouse_x-8,mouse_y-5);
			g.drawLine(mouse_x-9,mouse_y+5,mouse_x-8,mouse_y+5);
			g.drawLine(mouse_x+9,mouse_y-5,mouse_x+9,mouse_y+5);
			g.drawLine(mouse_x+9,mouse_y-5,mouse_x+8,mouse_y-5);
			g.drawLine(mouse_x+9,mouse_y+5,mouse_x+8,mouse_y+5);
		}
	}

	public void paint(Graphics g){
		g.setColor(BOUNDARY_COLOR);
		g.fillRect(0,0,windowWidth,windowHeight);
		g.setColor(BG_COLOR);
		g.fillRect(origin.getX(),origin.getY(),WIDTH,HEIGHT);
		g.setColor(Color.white);
		g.drawRect(origin.getX()-1,origin.getY()-1,WIDTH+1,HEIGHT+1);
		g.drawImage(BGIMAGE,origin.getX(),origin.getY(),null);
		for (Object v : visibleObjects){
			if (v instanceof Tile)
				v.paint(g);
			else if (!shift){
				v.paint(g);
			}
		}
		if (mode == 2){
			g.setColor(Color.red);
			g.drawLine(erasingTile.getX()+2,erasingTile.getY()+2,erasingTile.getX()+14,erasingTile.getY()+14);
			g.drawLine(erasingTile.getX()+14,erasingTile.getY()+2,erasingTile.getX()+2,erasingTile.getY()+14);
		}
		if (mode == 3){
			g.setColor(Color.gray);
			g.drawLine(solidTiler.getX(),solidTiler.getY()-3,solidTiler.getX(),solidTiler.getY()+3);
			g.drawLine(solidTiler.getX()-3,solidTiler.getY(),solidTiler.getX()+3,solidTiler.getY());
		}
		if (mode == 4){
			g.setColor(new Color(100,255,100));
			if (hud.getTeam() == 2)
				g.setColor(new Color(100,100,255));
			else if (hud.getTeam() == 1)
				g.setColor(new Color(255,100,100));
			g.drawRect(spawnTiler.getX()+1,spawnTiler.getY()+1,30,30);
			g.drawRect(spawnTiler.getX(),spawnTiler.getY(),32,32);
		}
		g.setColor(new Color(180,160,180));
		g.fillRect(0,0,192,windowHeight);
		g.setColor(new Color(220,220,200));
		g.drawLine(192,windowHeight,192,0);
		hud.paint(g);
		g.setColor(Color.black);
		g.drawString("DrawSnap On: " + snap,16,250);
		int jx = (-origin.getX()+setdrx);
		int jy = (-origin.getY()+setdry);
		g.drawString("("+jx+","+jy+")",16,270);
		g.setColor(Color.green.darker());
		drawMouse(g,mouse_x,mouse_y,true);
	}
}
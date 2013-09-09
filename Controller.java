import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.event.*; 
import java.awt.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Random;
import java.net.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class Controller extends Writer{

	private JFrame frame;
	private int windowWidth,windowHeight;
	private int WIDTH, HEIGHT;
	private int GAMEWIDTH = 640;
	private int GAMEHEIGHT = 480;

	public static final int VERSION = SpacedAge.VERSION;

	private Random rand;

	private Timer gameRunner;

	private boolean go;

	private Game game;

	private ArrayList<Object> objects;
	private ArrayList<Object> movingObjects;
	private ArrayList<Solid> solids;
	private ArrayList<Player> players;

	private ArrayList<Object> toRemove;

	private Player player;

	private Object view;
	private Object origin;

	private int mouse_x, mouse_y;

	private BufferedImage BGIMAGE;

	private Socket socket;
	private BufferedInputStream in;
	private BufferedOutputStream out;

	private Font font0;

	public static final double GRAVITY = 0.35;

	public Controller(JFrame frame){
		this.frame = frame;
		WIDTH = frame.getWidth();
		HEIGHT = frame.getHeight();
		
		connect();

		rand = new Random();

		font0 = new Font("Lucida Grande",Font.PLAIN,13);

		setFocusable(true);
		addKeyListener(new KeyboardInput());
		addMouseListener(new MouseInput());

		objects = new ArrayList<Object>();
		players = new ArrayList<Player>();
		movingObjects = new ArrayList<Object>();
		solids = new ArrayList<Solid>();

		go = false;

		origin = new Object(0,0,1,1);

		objects.add(origin);

		toRemove = new ArrayList<Object>();

		player = new Player(origin.getX()+32,origin.getY()+32,1);
		players.add(player);
		objects.add(player);
		movingObjects.add(player);

		view = new Object(32,32,1,1);

		gameRunner = new Timer(16,new GameRunner());
		gameRunner.start();
	}

	public boolean connect(){
        int port = 4546;
        String hostname = "128.61.28.242";
        try {
        	System.out.println("Connecting...");
            socket = new Socket(hostname, port);
            System.out.println("\nConnection successful thru "+hostname+":"+port);
	        socket.setTcpNoDelay(true);
            System.out.println("TCP No Delay: "+socket.getTcpNoDelay());
            out = new BufferedOutputStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());
            super.setStreams(out,in);
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+hostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not connect to '"+hostname+"'");
            System.exit(1);
        }
        return false;
	}

	public void addSolid(Solid s){
		solids.add(s);
		objects.add(s);
	}

	private class KeyboardInput implements KeyListener{
		public void keyPressed(KeyEvent e){
			switch (e.getKeyCode()) {
                case KeyEvent.VK_A :
                	player.setRight(false);
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D :
                	player.setLeft(false);
                    player.setRight(true);
                    break;
                case KeyEvent.VK_W :
                	if (!player.getDead()){
	                	player.changeY(1);
	                	for (Solid s : solids){
	                		if (checkCollision(player,s)){
	                    		player.setJump(true);
	                    		break;
	                    	}
	                	}
	                	player.changeY(-1);
	                	if (player.getJump()){
	                		try{
	                			writebyte(MSG_JUMP);
	                			writeshort(player.getX()-origin.getX());
	                			writeshort(player.getY()-origin.getY());
	                			sendmessage();
	                		} catch (IOException ioe){
	                			ioe.printStackTrace();
	                		}
	                	}
                	}
                    break;
                case KeyEvent.VK_S :
                	try{
                		writebyte(MSG_PLAYERDOWN);
                		writeboolean(true);
                		sendmessage();
                	} catch (IOException ioe){
                		ioe.printStackTrace();
                	}
                    player.setDown(true);
                    break;
            }
		}

		public void keyReleased(KeyEvent e){
			switch (e.getKeyCode()) {
                case KeyEvent.VK_A :
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D :
                    player.setRight(false);
                    break;
                case KeyEvent.VK_W :
                //	if (player.getdy() < -player.getJumpHeight()/2)
                //		player.setdy(-player.getJumpHeight()/2);
                    break;
                case KeyEvent.VK_S :
                	try{
                		writebyte(MSG_PLAYERDOWN);
                		writeboolean(false);
                		sendmessage();
                	} catch (IOException ioe){
                		ioe.printStackTrace();
                	}
                    player.setDown(false);
                    break;
                case KeyEvent.VK_ESCAPE:
                	close();
                	System.exit(0);
                	break;
            }
		}

		public void keyTyped(KeyEvent e){

		}
	}

	private class MouseInput implements MouseListener{
		public void mousePressed(MouseEvent e){
			if (!player.getDead()){
				player.setFiring(true);
				try{
					writebyte(MSG_FIRING);
					writeboolean(true);
					sendmessage();
				} catch (IOException ioe){
					System.err.println("Could not write.");
				}
			}
		}

		public void mouseReleased(MouseEvent e){
			if (player.getFiring()){
				player.setFiring(false);
				try{
					writebyte(MSG_FIRING);
					writeboolean(false);
					sendmessage();
				} catch (IOException ioe){
					System.err.println("Could not write.");
				}
			}
		}

		public void mouseClicked(MouseEvent e){
		}
		public void mouseEntered(MouseEvent e){
		}
		public void mouseExited(MouseEvent e){
		}
	}

	public void listen(){
		try{
			while (in.available() > 0){
//				System.out.print("In! Buffer size: "+in.available());
				zeroDefined();
			}
		} catch (IOException ioe){
			System.err.println("Connection lost.");
			System.exit(1);
		}
	}

	public class GameRunner implements ActionListener{
		public void actionPerformed(ActionEvent e){
			windowWidth  = frame.getContentPane().getWidth();
			windowHeight = frame.getContentPane().getHeight();

			listen();

			PointerInfo mo = MouseInfo.getPointerInfo();
			Point m = mo.getLocation();
			mouse_x = (int) m.getX() - frame.getX() - (frame.getWidth() - windowWidth)/2;
			mouse_y = (int) m.getY() - frame.getY() - (frame.getHeight() - windowHeight);

			player.setmdir(pointDirection(player.coordinates(),new Point(mouse_x,mouse_y)));

			update();
			repaint();
		}
	}

	public Player findPlayer(int pID){
		for (Player p : players){
			if (p.getID() == pID)
				return p;
		}
		return null;
	}

	public void loadMap(String map){
		System.out.println("Loading map: "+map);
		String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = "";
		try{
			decodedPath = URLDecoder.decode(path, "UTF-8");
			String delimiter = "/";
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(delimiter) + 1);
		} catch (Exception eee){}
		System.out.println("Here : "+decodedPath);
		try{
			BGIMAGE = ImageIO.read(new File(decodedPath+"/maps/"+map+".png"));
//			addSolid(new Solid(BGIMAGE.getWidth()/2,-16,BGIMAGE.getWidth(),32));
//			addSolid(new Solid(BGIMAGE.getWidth()/2,BGIMAGE.getHeight()+16,BGIMAGE.getWidth(),32));
//			addSolid(new Solid(-16,BGIMAGE.getHeight()/2,32,BGIMAGE.getHeight()+64));
//			addSolid(new Solid(BGIMAGE.getWidth()+16,BGIMAGE.getHeight()/2,32,BGIMAGE.getHeight()+64));
			System.out.println("Map image loaded.");
		} catch (IOException e){
			System.out.println("Could not load map image.");
		}

		try{
			System.out.println("Loading map data.");
			InputStream f = new FileInputStream(decodedPath+"/maps/"+map+".map");
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
					game.addSpawn(spawn);
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

	public void zeroDefined() throws IOException{
		int msgID = readbyte();
//		System.out.println(" | msgID : "+msgID);
		int pID = 0;
		Player p = null;
		switch(msgID){
			case MSG_KILL:
			int killed = readbyte();
			int killer = readbyte();
			for (Player pp : players){
				if (pp.getID() == killer){
					pp.addKill();
					if (game.getMode() < 3)
						pp.addScore(1);
				}
				else if (pp.getID() == killed){
					pp.setDead(true);
					pp.addDeath();
				}
			}
			break;

			case MSG_CLOSE:
			pID = readbyte();
			p = findPlayer(pID);
			toRemove.add(p);
			System.out.println("Player left. "+pID);
			break;

			case MSG_DIR:
			pID = readbyte();
			p = findPlayer(pID);
			p.setnmdir(Math.toRadians(readshort()));
			break;

			case MSG_PLAYERDOWN:
			pID = readbyte();
			p = findPlayer(pID);
			p.setDown(readboolean());
			break;

			case MSG_FIRING:
			pID = readbyte();
			p = findPlayer(pID);
			p.setFiring(readboolean());
			break;

			case MSG_SCORES:
			game.setScore(1,readbyte());
			game.setScore(2,readbyte());
			break;

			case MSG_JUMP:
			pID = readbyte();
			p = findPlayer(pID);
			p.setJump(true);
			p.setX(origin.getX()+readshort());
			p.setY(origin.getY()+readshort());
			break;

			case MSG_PLAYER:
			Player np = new Player(origin.getX()+64,origin.getY()+32,readbyte());
			np.setTeam(readbyte());
			np.setX(readushort());
			np.setY(readushort());
			np.setScore(readbyte());
			np.setKills(readbyte());
			np.setDeaths(readbyte());
			players.add(np);
			objects.add(np);
			movingObjects.add(np);
			System.out.println("New player. "+np.getID());
			bringToFront(player);
			break;

			case MSG_INFO:
			int serverVersion = readbyte();
			player.setID(readbyte());
			game = new Game(readstring(),readbyte());
			player.setTeam(readbyte());
			game.setRespawnTime(readbyte());
			System.out.println(serverVersion+","+VERSION);
			if (serverVersion != VERSION){
				System.out.println("Incompatible Version.");
				System.out.println("Local: "+VERSION/10.0+" | Server: "+serverVersion/10.0);
				close();
			}
			loadMap(game.getMapName());
			resetPlayer();
			bringToFront(player);
			go = true;
			break;

			case MSG_HIT:
			player.changeHP(-readbyte());
			shakeView(12);
			break;

			case MSG_MOVEPLAYER:
			pID = readbyte();
			int pDir = readbyte();
			p = findPlayer(pID);
			p.setLeft(false);
			p.setRight(false);
			p.setDead(false);
			if (pDir == 0){
				p.setX(origin.getX()+readshort());
				p.setY(origin.getY()+readshort());
			}
			if (pDir == Player.LEFT){
				p.setLeft(true);
			}
			else if (pDir == Player.RIGHT){
				p.setRight(true);
			}
			break;
		}
	}

	public void moveObjects(){

		for (Player p : players){
			if (p.getFiring() && p.getRecoil() == 0){
				Weapon wep = p.getWeapon();
				Bullet bul = new Bullet(p.getX(),p.getY(),p.mdir,p.getID(),wep.getPower());
				movingObjects.add(bul);
				objects.add(bul);
				p.setRecoil(wep.getRecoil());
				int shake = 6;
				shake -= (pointDistance(p.coordinates(),player.coordinates())/40.0-4);
				if (shake > 6)
					shake = 6;
				if (shake > 0)
					shakeView(shake);
			}
		}

		for (Object o :objects){
			o.setMove();
			if (o.getLife() <= 0)
				toRemove.add(o);
		}

		for (Object o : movingObjects){
			if (o instanceof Bullet){
				int source = ((Bullet)o).getOwner();
				int sourceTeam = 0;
				for (Player p : players){
					if (p.getID() == source){
						sourceTeam = p.getTeam();
						break;
					}
				}
				if (sourceTeam == 0)
					sourceTeam = 4;
				for (Solid sb : solids)
					if (checkCollision(sb,o)){
						Dust dust = new Dust(o.getX(),o.getY());
						objects.add(dust);
						toRemove.add(o);
						break;
					}
				for (Player p : players){
					if (!p.getDead() && source != p.getID() && checkCollision(p,o) && p.getTeam() != sourceTeam){
						if (source == player.getID()){
							try{
								writebyte(MSG_HIT);
								writebyte(p.getID());
								writebyte(((Bullet)o).getPower());
								sendmessage();
							} catch (IOException e){
								System.err.println("Could not hit.");
							}
						}
						Dust dust = new Dust(o.getX(),o.getY());
						objects.add(dust);
						toRemove.add(o);
					}
				}
				if (((Bullet)o).getLife() <= 0){
					toRemove.add(o);
				}
			}
			else{
				o.changeY(o.getdy());
				for (Solid s : solids){
					if (checkCollision(o,s)){
						while (checkCollision(o,s)){
							if (o.getdy() > 0)
								o.changeY(-1);
							else
								o.changeY(1);
						}
						o.setdy(0);
//						break;
					}
				}
				o.changeX(o.getdx());
				for (Solid s : solids){
					if (checkCollision(o,s)){
						while (checkCollision(o,s)){
							if (o.getdx() > 0)
								o.changeX(-1);
							else
								o.changeX(1);
						}
						o.setdx(0);
//						break;
					}
				}
			}
		}
	}

	public void resetPlayer(){
		player.setLeft(false);
		player.setRight(false);
		int[] spawnPoint = null;
		while (spawnPoint == null){
			int check = rand.nextInt(game.getSpawns().size());
			int[] chSpawn = game.getSpawns().get(check);
			if (chSpawn[0] == 0 || chSpawn[0] == player.getTeam())
				spawnPoint = chSpawn;
		}
		player.setMove();
		player.setX(origin.getX()+spawnPoint[1]);
		player.setY(origin.getY()+spawnPoint[2]);
		player.setHP(player.getMaxHP());
		player.setDead(false);
		try{
			writebyte(MSG_MOVEPLAYER);
			writebyte(player.getFDir());
			if (player.getFDir() == 0){
				writeshort(player.getX() - origin.getX());
				writeshort(player.getY() - origin.getY());
			}
			sendmessage();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void shakeView(int shake){
		double sdir = rand.nextInt(200)/100.0 * Math.PI;
		int ch_x = (int)lengthdir_x(shake,sdir);
		int ch_y = (int)lengthdir_y(shake,sdir);
		for (Object o : objects){
			o.changeX(-Math.ceil(ch_x));
			o.changeY(-Math.ceil(ch_y));
		}	
	}

	public void setView(){
		double vDir = player.mdir;
		int vDist = (int)pointDistance(player.coordinates(),new Point(mouse_x,mouse_y))/3;
		view.setX(player.getX()+(int)lengthdir_x(vDist,vDir));
		view.setY(player.getY()+(int)lengthdir_y(vDist,vDir));

		if (view.getX() < origin.getX() + getWindowWidth()/2)
			view.setX(origin.getX() + getWindowWidth()/2);
		else if (view.getX() > origin.getX() + BGIMAGE.getWidth() - getWindowWidth()/2)
			view.setX(origin.getX() + BGIMAGE.getWidth() - getWindowWidth()/2);

		if (view.getY() < origin.getY() + getWindowHeight()/2)
			view.setY(origin.getY() + getWindowHeight()/2);
		else if (view.getY() > origin.getY() + BGIMAGE.getHeight() - getWindowHeight()/2)
			view.setY(origin.getY() + BGIMAGE.getHeight() - getWindowHeight()/2);

		Object follower = view;
		if (follower.getX() != getWindowWidth()/2 || follower.getY() != getWindowHeight()/2){
			int ch_x = follower.getX() - getWindowWidth()/2;
			int ch_y = follower.getY() - getWindowHeight()/2;
			for (Object o : objects){
				o.changeX(-Math.ceil(ch_x/3.0));
				o.changeY(-Math.ceil(ch_y/3.0));
			}
		}
	}

	public void update(){
		if (go){
			player.parseMovement();

			if (player.getHP() <= 0 && !player.getDead()){
				player.setDead(true);
				player.setFiring(false);
				player.setRespawn(game.getRespawnTime()*60);
				try{
					writebyte(MSG_KILL);
					sendmessage();
				} catch (IOException e){
					e.printStackTrace();
				}
			}

			else if (player.getDead() && player.getRespawn() <= 0){
				resetPlayer();
			}

			try{
				if (player.getChanged()){
					writebyte(MSG_MOVEPLAYER);
					writebyte(player.getFDir());
					if (player.getFDir() == 0){
						writeshort(player.getX() - origin.getX());
						writeshort(player.getY() - origin.getY());
					}
					sendmessage();
				}
				if (player.getSendDir()){
					writebyte(MSG_DIR);
					writeshort((int)Math.toDegrees(player.mdir));
					sendmessage();
					player.setSendDir(false);
				}
			} catch (IOException ioe){
				System.err.println("Connection lost!");
				close();
			}

			moveObjects();

			setView();
		}
		if (toRemove.size() > 0)
			removeObjects(toRemove);
		toRemove.clear();
	}

	public void removeObjects(ArrayList<Object> list){
		for (Object i : list){
			objects.remove(i);
			movingObjects.remove(i);
			solids.remove(i);
			players.remove(i);
			i = null;
		}
	}

	public void bringToFront(Object o){
		objects.remove(o);
		objects.add(o);
	}

	public int getWindowWidth(){
		return windowWidth;
	}

	public int getWindowHeight(){
		return windowHeight;
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

	public static void fillSlantRect(Graphics g, int stx, int sty, int width, int height, int slope){
		int ch = 1;
		if (width < 0)
			ch = -1;
		for (int i = 0; i < Math.abs(width); i++){
			g.drawLine(stx,sty,stx+height*slope,sty+height);
			stx += ch;
		}
	}

	public static void drawSlantRect(Graphics g, int stx, int sty, int width, int height, int slope){
		g.drawLine(stx,sty,stx+width,sty);
		g.drawLine(stx+height*slope,sty+height,stx+width+height*slope,sty+height);
		g.drawLine(stx,sty,stx+height*slope,sty+height);
		g.drawLine(stx+width,sty,stx+width+height*slope,sty+height);
	}

	public void close(){
		try{
			out.close();
		    in.close();
		    socket.close();
		    System.out.println("Disconnected.");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    System.exit(0);
	}

	public void paint(Graphics g2d){
	//	BufferedImage dispImage = new BufferedImage(GAMEWIDTH,GAMEHEIGHT,BufferedImage.TYPE_INT_RGB);
	//    Graphics g = dispImage.getGraphics();
		Graphics2D g = (Graphics2D)g2d;
		g.setFont(font0);
		g.setColor(Color.black);
		g.fillRect(0,0,WIDTH,HEIGHT);
		if (BGIMAGE != null){
			//g.drawImage(BGIMAGE,origin.getX(),origin.getY(),null);
		}
		for (Object o : objects){
			o.paint(g);
		}
		g.setColor(new Color(80,80,80));
		fillSlantRect(g,424,24,24,24,-1);
		fillSlantRect(g,216,24,-24,24,1);
		g.fillRect(320-104,24,208,25);
		g.setColor(new Color(0,160,0));
		fillSlantRect(g,344,24,(int)(104*(player.getHP()/(player.getMaxHP()+0.0))),24,-1);
		g.setColor(new Color(180,180,180));
		drawSlantRect(g,344,24,104,24,-1);

		if (go){
			game.paint(g,player.getTeam());
		}

		g.setColor(Color.green);
		drawMouse(g,mouse_x,mouse_y,false);
		g.drawRect(origin.getX(),origin.getY(),1,1);
	//	double scale = 1;
	//	gg.drawImage(dispImage,0,0,(int)(640),(int)(480),0,0,640,480,null);
	}
}
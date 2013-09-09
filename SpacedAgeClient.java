import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;

public class SpacedAgeClient extends Writer implements ActionListener{

	private Socket socket = null;
	private BufferedOutputStream out;
	private BufferedInputStream in;

	private boolean disconnect = false;

	private int x = 0, y = 0;

	public static final int VERSION = SpacedAgeServer.VERSION;
	public static String map = SpacedAgeServer.map;
	public static int mode = SpacedAgeServer.mode;

	private int ID;
	private int lastHitID;
	private int team;
	private int score, kills, deaths;

	private Timer runner;

	private ServerGame game;

	private ArrayList<SpacedAgeClient> clients;

	public SpacedAgeClient(Socket socket, int id, ArrayList<SpacedAgeClient> clients, ServerGame game){
		this.socket = socket;
		this.game = game;
		try{
			this.socket.setSoTimeout(10000);
		} catch (SocketException se){
			se.printStackTrace();
		}
		team = 0;
		score = 0;
		kills = 0;
		deaths = 0;

		this.ID = id;
		lastHitID = id;
		this.clients = clients;

		if (game.hasTeams()){
			int team1 = 0;
			int team2 = 0;
			for (SpacedAgeClient c : clients){
				if (c.getTeam() == 1)
					team1 += 1;
				else if (c.getTeam() == 2)
					team2 += 1;
			}
			if (team1 > team2)
				team = 2;
			else
				team = 1;
		}

		try {
			System.out.println("Client connected");
		    out = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));
		    in = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
		    super.setStreams(out,in);

		    runner = new Timer(8,this);
		    runner.start();

		    writebyte(MSG_INFO);
		    writebyte(VERSION);
		    writebyte(getID());
		    writestring(map);
		    writebyte(mode);
		    writebyte(team);
		    writebyte(game.getRespawnTime());
		    sendmessage();
		} catch (IOException e) {
			System.err.println("Failed to connect client.");
		    e.printStackTrace();
		}

		try{
			for (SpacedAgeClient c : clients){
		    	if (c.getID() != getID()){
		    		c.writebyte(MSG_PLAYER);
		    		c.writebyte(getID());
		    		c.writebyte(getTeam());
		    		c.writeushort(getX());
		    		c.writeushort(getY());
		    		c.writebyte(getScore());
		    		c.writebyte(getKills());
		    		c.writebyte(getDeaths());
		    		c.sendmessage();
		    		writebyte(MSG_PLAYER);
		    		writebyte(c.getID());
		    		writebyte(c.getTeam());
		    		writeushort(c.getX());
		    		writeushort(c.getY());
		    		writebyte(c.getScore());
		    		writebyte(c.getKills());
		    		writebyte(c.getDeaths());
		    		sendmessage();
		    	}
		    }
		    game.sendScore(this);
		} catch (IOException ioe){
			System.err.println("Failed to send client's data.");
		    ioe.printStackTrace();
		}
	}

	public void close(){
		try{
			disconnect = true;
			out.close();
		    in.close();
		    socket.close();
		    for (SpacedAgeClient c : clients){
		    	if (getID() != c.getID()){
			    	c.writebyte(MSG_CLOSE);
			    	c.writebyte(getID());
			    	c.sendmessage();
		    	}
		    }
		    System.out.println("Client disconnected: "+getID());
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	public void addKill(){
		kills += 1;
		if (game.getMode() < 3){
			score += 1;
			game.changeScore(team,1);
		}
	}

	public int getScore(){
		return score;
	}

	public int getKills(){
		return kills;
	}

	public int getDeaths(){
		return deaths;
	}

	public int getID(){
		return ID;
	}

	public int getTeam(){
		return team;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void setLastHitID(int id){
		lastHitID = id;
	}

	public void zeroDefined() throws IOException{
		int msgID = readbyte();
//		System.out.println(" | msgID : "+msgID);
		switch(msgID){
			case MSG_KILL:
			deaths += 1;
			for (SpacedAgeClient c : clients){
				if (c.getID() == lastHitID){
					c.addKill();
					break;
				}
			}
			for (SpacedAgeClient c : clients){
				c.writebyte(MSG_KILL);
				c.writebyte(getID());
				c.writebyte(lastHitID);
				c.sendmessage();
			}
			break;

			case MSG_HIT:
			int hitID = readbyte();
			int damage = readbyte();
			for (SpacedAgeClient c : clients){
				if (c.getID() == hitID){
					c.setLastHitID(getID());
					c.writebyte(MSG_HIT);
					c.writebyte(damage);
					c.sendmessage();
					break;
				}
			}
			break;

			case MSG_PLAYERDOWN:
			boolean down = readboolean();
			for (SpacedAgeClient c : clients){
				if (c.getID() != getID()){
					c.writebyte(MSG_PLAYERDOWN);
					c.writebyte(getID());
					c.writeboolean(down);
					c.sendmessage();
				}
			}
			break;

			case MSG_FIRING:
			boolean firing = readboolean();
			for (SpacedAgeClient c : clients){
				if (c.getID() != getID()){
					c.writebyte(MSG_FIRING);
					c.writebyte(getID());
					c.writeboolean(firing);
					c.sendmessage();
				}
			}
			break;

			case MSG_DIR:
			int mdir = readshort();
			for (SpacedAgeClient c : clients){
				if (c.getID() != getID()){
					c.writebyte(MSG_DIR);
					c.writebyte(getID());
					c.writeshort(mdir);
					c.sendmessage();
				}
			}
			break;

			case MSG_JUMP:
			x = readshort();
			y = readshort();
			for (SpacedAgeClient c : clients){
				if (c.getID() != getID()){
					c.writebyte(MSG_JUMP);
					c.writebyte(getID());
					c.writeshort(x);
					c.writeshort(y);
					c.sendmessage();
				}
			}
			break;

			case MSG_MOVEPLAYER:
			int pDir = readbyte();
			if (pDir == 0){
				x = readshort();
				y = readshort();
			}
//			System.out.println(x+", "+y);
			for (SpacedAgeClient c : clients){
				if (c.getID() != getID()){
					c.writebyte(MSG_MOVEPLAYER);
					c.writebyte(getID());
					c.writebyte(pDir);
					if (pDir == 0){
						c.writeshort(x);
						c.writeshort(y);
					}
					c.sendmessage();
				}
			}
			break;
		}
	}

	public void sendmessage(){
		if (!disconnect){
			try{
				super.sendmessage();
			} catch (IOException ioe){
				System.out.println("Socket lost, closing.");
				close();
			}
		}
	}

	public void actionPerformed(ActionEvent ae){
		if (disconnect){
			clients.remove(this);
			runner.stop();
		}
		else{
			try{
					while (in.available() > 0){
//						System.out.print("In! |"+getID()+"| Buffer size: "+in.available());
						zeroDefined();
					}
			} catch (IOException e) {
				System.out.println("Disconnect: "+getID());
			    e.printStackTrace();
			    close();
			}
		}
	}
}
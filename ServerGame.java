import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;

public class ServerGame extends Writer implements ActionListener{

	private int mode;
	private int[] scores;
	private int[] oldScores;
	private ArrayList<SpacedAgeClient> clients;
	private Timer runner;
	private boolean hasTeams;
	private int objectiveStat;
	private int respawnTime;

	public ServerGame(int mode, ArrayList<SpacedAgeClient> clients){
		this.clients = clients;
		this.mode = mode;
		scores = new int[3];
		oldScores = new int[3];
		scores[0] = 0;
		scores[1] = 0;
		scores[2] = 0;
		setOldScores();
		oldScores[1] = 1;

		respawnTime = 5;

		hasTeams = false;
		if (mode > 1)
			hasTeams = true;
		if (mode == 4)
			objectiveStat = 2000;

		runner = new Timer(50,this);
		runner.start();
	}

	public int getRespawnTime(){
		return respawnTime;
	}

	public int getObjectiveStat(){
		return objectiveStat;
	}

	public boolean hasTeams(){
		return hasTeams;
	}

	public void actionPerformed(ActionEvent e){
		boolean same = true;
		for (int i = 1; i < 3; i++){
			if (scores[i] != oldScores[i]){
				same = false;
			}
		}

		if (!same){
			try{
				for (SpacedAgeClient c : clients){
					sendScore(c);
				}
			} catch (IOException ioe){
				System.out.println("Could not write.");
			}
		}
		setOldScores();
	}

	public void setOldScores(){
		for (int i = 0; i < 3; i++)
			oldScores[i] = scores[i];
	}

	public void sendScore(SpacedAgeClient c) throws IOException{
		c.writebyte(MSG_SCORES);
		c.writebyte(scores[1]);
		c.writebyte(scores[2]);
		c.sendmessage();
	}

	public int getMode(){
		return mode;
	}

	public void changeScore(int i, int ds){
		scores[i] += ds;
	}

	public void setScore(int i, int s){
		scores[i] = s;
	}

	public int getScore(int i){
		return scores[i];
	}

	public void resetScores(){
		for (int i = 0; i < 3; i++)
			setScore(i,0);
	}
}
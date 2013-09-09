/*
	Custom Class by Preston Turner for reading and writing information to a stream.

		Methods:
			setStreams(out,in) !!
			sendmessage()
			writebyte(int)
			writeshort(int)
			writestring(String)
			readbyte()
			readshort()
			readstring()

*/

import java.io.*;
import javax.swing.JComponent;

public abstract class Writer extends JComponent{
	private BufferedInputStream in;
	private BufferedOutputStream out;

	public final int MSG_PLAYER = 1;
	public final int MSG_INFO = 2;
	public final int MSG_MOVEPLAYER = 3;
	public final int MSG_JUMP = 4;
	public final int MSG_DIR = 5;
	public final int MSG_FIRING = 6;
	public final int MSG_CLOSE = 7;
	public final int MSG_PLAYERDOWN = 8;
	public final int MSG_HIT = 9;
	public final int MSG_KILL = 10;
	public final int MSG_SCORES = 11;

	public Writer(){}

	/* MUST be called before any other methods!! */
	public void setStreams(BufferedOutputStream out, BufferedInputStream in){
		this.in = in;
		this.out = out;
	}

	/* Sends the buffered message */
	public void sendmessage() throws IOException{
		out.flush();
	}

	/* Reads a 1-byte integer (0-255) */
	public int readbyte() throws IOException{
		int num = 0;
		if (in.available() > 0)
			num = in.read();
		else
			System.out.println("Invalid reciept");
		return num;
	}

	/* Writes a 1-byte integer (0-255) */
	public void writebyte(int num) throws IOException{
		out.write(num);
	}

	/* Reads a 2-byte signed integer */
	public int readshort() throws IOException{
		int num = 0;
		if (in.available() >= 2){
			num = (readbyte()*255);
			num += readbyte();
			num -= (int)(65536/2);
		}
		else{
			System.out.println("Invalid reciept");
		}
		return num;
	}

	/* Writes a 2-byte signed integer */
	public void writeshort(int num) throws IOException{
		num += (int)(65536/2);
		writebyte((int)(num/255));
		writebyte(num%255);
	}

	/* Reads a 2-byte UNSIGNED integer */
	public int readushort() throws IOException{
		int num = 0;
		if (in.available() >= 2){
			num = (in.read()*255);
			num += in.read();
		}
		else{
			System.out.println("Invalid reciept");
		}
		return num;
	}

	/* Writes a 2-byte UNSIGNED integer */
	public void writeushort(int num) throws IOException{
		out.write((int)(num/255));
		out.write(num%255);
	}

	/* Reads a String */
	public String readstring() throws IOException{
		String string = "";
		int len = readbyte();
		for (int i = 0; i<len; i++){
			int b = readbyte();
			string += ""+Character.toChars(b)[0];
		}
		return string;
	}

	/* Writes a String */
	public void writestring(String string) throws IOException{
		writebyte(string.length());
		byte[] array = string.getBytes();
		for (int i : array){
			writebyte(i);
		}
	}

	/* Reads a boolean */
	public boolean readboolean() throws IOException{
		if (in.available() > 0)
			return (readbyte() == 1);
		else{
			System.out.println("Invalid reciept");
			return false;
		}
	}

	/* Writes a boolean */
	public void writeboolean(boolean bool) throws IOException{
		if (bool)
			writebyte(1);
		else
			writebyte(0);
	}
}
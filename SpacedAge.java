import java.awt.Graphics;
import java.awt.GraphicsDevice;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.image.*;
//import java.awt.Cursor;
import java.awt.*;
import java.net.*;
import java.io.*;

public class SpacedAge{

    public static final int VERSION = 10;

	public static void main(String[] args){
		System.out.println("Initializing...");
		JFrame frame = new JFrame("Spaced Age");

		int frameWidth = 640;
		int frameHeight = 480;
		frame.setSize(frameWidth+frame.getContentPane().getWidth(),frameHeight+frame.getContentPane().getHeight());
		frame.setResizable(false);
		frame.setLocation(240,60);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

/*		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	GraphicsDevice device = env.getDefaultScreenDevice();
    	GraphicsConfiguration config = device.getDefaultConfiguration();
    	BufferedImage buffy = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
*/
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Blank Cursor");
		frame.getContentPane().setCursor(blankCursor);
//      frame.setAlwaysOnTop(true);

        frame.getContentPane().add(new Controller(frame));

		frame.setVisible(true);

//		System.gc();
		System.out.println("Running...");
	}
}
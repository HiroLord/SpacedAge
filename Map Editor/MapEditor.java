import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.image.*;
//import java.awt.Cursor;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class MapEditor{
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		int mapWidth = 800;
		int mapHeight = 480;
		System.out.print("\nLoad map (no extensions) (leave blank for new map): ");
		String mapName = input.nextLine();
		if (mapName.equals("")){
			System.out.print("Map Width: ");
			mapWidth = input.nextInt();
			System.out.print("Map Height: ");
			mapHeight = input.nextInt();
		}
		System.out.println("\nInitializing...");
		JFrame frame = new JFrame("Spaced Age");

		int frameWidth = 800;
		int frameHeight = 600;
		frame.setSize(frameWidth+frame.getWidth(),frameHeight+frame.getHeight());
		frame.setResizable(false);
		frame.setLocation(240,60);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Blank Cursor");
		frame.getContentPane().setCursor(blankCursor);

        frame.getContentPane().add(new Controller(frame,mapWidth,mapHeight,mapName));
		frame.setVisible(true);

		System.gc();
		System.out.println("Running...");
	}
}
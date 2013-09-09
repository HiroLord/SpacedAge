import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SpacedAgeServer{

	private static final int PORT = 4546;
    public static final int VERSION = 10;
    public static String map;
    public static int mode;
    private static ServerGame game;

	public static void main(String[] args) throws IOException{
        
        int connections = 0;
		ServerSocket serverSocket = null;
        ArrayList<SpacedAgeClient> clients = new ArrayList<SpacedAgeClient>();

        try {
            Scanner input = new Scanner(System.in);
            System.out.print("Map to load: ");
            map = input.nextLine();
            System.out.print("Game mode (1 = DeathMath, 2 = Team DeathMatch, 3 = CTF, 4 = Core Defense): ");
            mode = input.nextInt();
            game = new ServerGame(mode,clients);
            serverSocket = new ServerSocket(PORT);
            System.out.println("Spaced Age Server v"+VERSION/10.0+" running on port "+PORT);
        } catch (IOException e) {
            System.err.println("Could not open listening port: " + PORT);
            System.exit(1);
        }

        boolean listening = true;

        while (listening)
	    	clients.add(new SpacedAgeClient(serverSocket.accept(),(connections = connections + 1),clients,game));

		serverSocket.close();

	}
}
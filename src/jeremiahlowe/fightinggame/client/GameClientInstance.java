package jeremiahlowe.fightinggame.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.Player;
import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import processing.core.PApplet;
import processing.core.PVector;

public class GameClientInstance extends GraphicalInstance {
	private Socket socket;
	private InputStream in;
	private PrintWriter out;
	
	public Player localPlayer;
	
	public GameClientInstance(PApplet applet) {
		super(applet);
	}

	public boolean connectToServer(String host, int port) {
		try{
			socket = new Socket(host, port);
			in = socket.getInputStream();
			out = new PrintWriter(socket.getOutputStream());
			System.out.println("Sucesfully connected to server!");
			return true;
		}catch(IOException ioe) {
			System.out.println("Failed to connect to server " + host + " on port " + port);
			System.err.println(ioe);
			return false;
		}
	}
	public void updateLocalPlayer() {
		Gson json = new Gson();
		String jsonstr = json.toJson(new RemotePlayer(localPlayer));
		out.println("Hello server!");
		try {
			socket.getOutputStream().write("Moar test data!\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class RemotePlayer{
	public PVector keys, look;
	public boolean shooting;
	
	public RemotePlayer(Player from) {
		keys = from.keys;
		look = from.look;
		shooting = from.shooting;
	}
}

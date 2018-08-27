package jeremiahlowe.fightinggame.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jeremiahlowe.fightinggame.Player;
import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import processing.core.PApplet;
import processing.core.PVector;

public class GameClientInstance extends GraphicalInstance {
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	public Player localPlayer;
	
	public GameClientInstance(PApplet applet) {
		super(applet);
	}

	public boolean connectToServer(String host, int port) {
		try{
			socket = new Socket(host, port);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			return true;
		}catch(IOException ioe) {
			return false;
		}
	}
	public boolean sendPos(String name, PVector p) {
		String msg = name;
		msg += ":";
		msg += p.x;
		msg += ",";
		msg += p.y;
		msg += "\n";
		try{
			out.write(msg.getBytes());
			return true;
		}catch(IOException ioe) {
			return false;
		}
	}
}

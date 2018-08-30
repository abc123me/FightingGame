package jeremiahlowe.fightinggame.client;

import java.io.IOException;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.SocketCommunicator;
import jeremiahlowe.fightinggame.phys.PhysicsObject;
import jeremiahlowe.fightinggame.phys.Player;
import jeremiahlowe.fightinggame.util.Timing;
import processing.core.PApplet;
import processing.core.PVector;

public class GameClientInstance extends GraphicalInstance {
	private SocketCommunicator scomm;
	
	public Player localPlayer;
	public final Gson gson;
	
	public GameClientInstance(PApplet applet) {
		super(applet);
		gson = new Gson();
	}

	@Override
	public void addPhysicsObject(PhysicsObject p) {
		return;
	}
	@Override
	public void removePhysicsObject(PhysicsObject p) {
		return;
	}
	
	public boolean connectToServer(String host, int port) {
		try{
			scomm = new SocketCommunicator(host, port);
			System.out.println("Sucesfully connected to server!");
			return true;
		}catch(IOException ioe) {
			System.out.println("Failed to connect to server " + host + " on port " + port);
			System.err.println(ioe);
			return false;
		}
	}
	public Player getPlayerFromServer() {
		Packet p = waitForRequest(1000, EPacketIdentity.PLAYER_DATA);
		if(p == null)
			return null;
		System.out.println(p.contents);
		Player local = gson.fromJson(p.contents, Player.class);
		local.instance = this; //this is VITAL since instance will always be null to it's transient property
		return local;
	}
	public void updateLocalPlayer() {
		String jsonPlayer = gson.toJson(new RemotePlayer(localPlayer));
		Packet p = Packet.createUpdate(EPacketIdentity.PLAYER_DATA, jsonPlayer);
		scomm.println(gson.toJson(p));
	}
	public int getServerVersion() {
		Packet p = waitForRequest(1000, EPacketIdentity.VERSION_DATA);
		if(p == null)
			return -1;
		return Integer.parseInt(p.contents);
	}
	
	private Packet waitForRequest(long timeout, EPacketIdentity identity) {
		scomm.println(gson.toJson(Packet.createRequest(identity)));
		if(!waitForServer(1000)) {
			System.err.println("Server didn't respond to request for \"" + identity + "\" after " + timeout + "ms");
			return null;
		}
		String fromServer = scomm.readLine();
		Packet p = gson.fromJson(fromServer, Packet.class);
		if(p.type != Packet.UPDATE){
			System.err.println("Server responded to \"" + identity + "\" request with an invalid packet type of: " + p.type);
			return null;
		}
		if(p.identity != identity){
			System.err.println("Server responded to \"" + identity + "\" request with an invalid packet identity of \"" + p.identity + "\"");
			return null;
		}
		return p;
	}
	private boolean waitForServer(long timeout) {
		return waitForServer(timeout, 10);
	}
	private boolean waitForServer(long timeout, long step) {
		long time = 0;
		while(!scomm.hasNext()) {
			if(!Timing.sleep(step))
				return false;
			time += step;
			if(time > timeout)
				return false;
		}
		return true;
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

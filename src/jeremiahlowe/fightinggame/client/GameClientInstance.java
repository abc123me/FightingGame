package jeremiahlowe.fightinggame.client;

import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.PlayerMovementData;
import jeremiahlowe.fightinggame.phys.PhysicsObject;
import jeremiahlowe.fightinggame.phys.Player;
import jeremiahlowe.fightinggame.server.SocketWrapperThread;
import processing.core.PApplet;

public class GameClientInstance extends GraphicalInstance implements ISocketListener{
	private SocketWrapperThread scomm;
	
	public Player localPlayer;
	public final Gson gson;
	
	public GameClientInstance(PApplet applet) {
		super(applet);
		gson = new Gson();
	}
	
	@SuppressWarnings("resource") //Will be closed by either hook or disconnect
	public boolean connectToServer(String host, int port) {
		try{
			scomm = new SocketWrapperThread(0, new Socket(host, port));
			scomm.addClientListener(this);
			scomm.start();	
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					scomm.close();
				}
			});
			return true;
		}catch(IOException ioe) {
			System.out.println("Failed to connect to server " + host + " on port " + port);
			System.err.println(ioe);
			return false;
		}
	}
	public void disconnect() {
		scomm.close();
	}
	public void onConnect(SocketWrapperThread cw) {
		System.out.println("Sucesfully connected to server!");
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.VERSION_DATA)
			scomm.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		System.out.println("Got update from server!");
		if(p.identity == EPacketIdentity.PLAYER_ADD) {
			Player pl = gson.fromJson(p.contents, Player.class);
			System.out.println("Adding new player based off of: " + p.contents);
			if(pl != null) add(pl);
			else throw new RuntimeException("Server sent us invalid playerdata?!");
		}
		else if(p.identity == EPacketIdentity.PLAYER_REMOVE) {
			Player pl = gson.fromJson(p.contents, Player.class);
			System.out.println("Removing player based off of: " + p.contents);
			if(pl != null) remove(pl);
			else throw new RuntimeException("Server sent us invalid playerdata?!");
		}
		else if(p.identity == EPacketIdentity.PLAYER_MOVEMENT) {
			PlayerMovementData pd = gson.fromJson(p.contents, PlayerMovementData.class);
			if(pd == null) throw new RuntimeException("Server sent us invalid playerdata?!");
			System.out.println("Updating player " + pd.forUUID + "'s movement data on the client");
			Player pl = getByUUID(pd.forUUID);
			pd.copyTo(pl);
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		System.out.println("Disconnected from server!");
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		
	}
	public void onReceiveUnknownPacket(SocketWrapperThread clientWrapper, Packet p) {
		System.out.println("Got an unknown packet???!!!");
	}
	public Player getByUUID(long uuid) {
		for(PhysicsObject p : physicsObjects)
			if(p != null && p instanceof Player)
				if(((Player) p).uuid == uuid)
					return (Player) p;
		return null;
	}
	public Player getLocalPlayerFromServer() {
		scomm.sendPacket(Packet.createRequest(EPacketIdentity.CLIENT_PLAYER_DATA));
		Packet p = scomm.waitForUpdate(1000, EPacketIdentity.CLIENT_PLAYER_DATA);
		if(p == null)
			return null;
		return gson.fromJson(p.contents, Player.class);
	}
	public void updateLocalPlayer() {
		scomm.sendPacket(
				Packet.createUpdate(EPacketIdentity.PLAYER_MOVEMENT, //Packet type
				gson.toJson(new PlayerMovementData(localPlayer)))); //Movement data
	}
}

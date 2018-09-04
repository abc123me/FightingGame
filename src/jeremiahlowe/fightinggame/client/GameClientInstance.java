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
	
	public boolean connectToServer(String host, int port) {
		try{
			scomm = new SocketWrapperThread(0, new Socket(host, port));
			scomm.start();	
			System.out.println("Sucesfully connected to server!");
			return true;
		}catch(IOException ioe) {
			System.out.println("Failed to connect to server " + host + " on port " + port);
			System.err.println(ioe);
			return false;
		}
	}

	public void onConnect(SocketWrapperThread cw) {
		return;
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.VERSION_DATA)
			scomm.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
		return;
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		System.out.println("Got update from server!");
		if(p.identity == EPacketIdentity.PLAYER_ADD) {
			Player pl = gson.fromJson(p.contents, Player.class);
			System.out.println("Adding new player based off of: " + p.contents);
			if(pl != null) add(pl);
			else throw new RuntimeException("Server sent us invalid playerdata?!");
		}
		if(p.identity == EPacketIdentity.PLAYER_REMOVE) {
			Player pl = gson.fromJson(p.contents, Player.class);
			System.out.println("Removing player based off of: " + p.contents);
			if(pl != null) remove(pl);
			else throw new RuntimeException("Server sent us invalid playerdata?!");
		}
		return;
	}
	public void onDisconnect(SocketWrapperThread cw) {
		return;
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		return;
	}
	public void onReceiveUnknownPacket(SocketWrapperThread clientWrapper, Packet p) {
		return;
	}
	public Player getLocalPlayerFromServer() {
		Packet p = scomm.waitForPacket(1000, EPacketIdentity.LOCAL_PLAYER_DATA);
		if(p == null)
			return null;
		return gson.fromJson(p.contents, Player.class);
	}
	public void updateLocalPlayer() {
		scomm.sendPacket(
				Packet.createUpdate(EPacketIdentity.LOCAL_PLAYER_MOVEMENT, //Packet type
				gson.toJson(new PlayerMovementData(localPlayer)))); //Movement data
	}
}

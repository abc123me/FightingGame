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
import jeremiahlowe.fightinggame.ui.IStatistic.ITextStatistic;
import processing.core.PApplet;

public class GameClientInstance extends GraphicalInstance implements ISocketListener {
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
					System.out.println("Bye from shutdownHook()");
					scomm.interrupt();
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
			if(pl == null) throw new RuntimeException("Server sent us invalid playerdata?!");
			if(pl.uuid == localPlayer.uuid) {
				System.out.println("Player being added has same UUID as localPlayer, Ignoring it!");
				return;
			}
			Player pu = getPlayerWithUUID(pl.uuid);
			if(pu != null) {
				System.out.println("Duplicated player detected, fucking it off!");
				remove(pu);
			} //Either way add the player
			add(pl);
		}
		else if(p.identity == EPacketIdentity.PLAYER_REMOVE) {
			long uuid = Long.parseLong(p.contents);
			System.out.println("Removing player based off of UUID: " + uuid);
			Player pl = getPlayerWithUUID(uuid);
			if(pl == null) System.out.println("No player with UUID: " + uuid);
			else remove(pl);
		}
		else if(p.identity == EPacketIdentity.PLAYER_MOVEMENT) {
			PlayerMovementData pd = gson.fromJson(p.contents, PlayerMovementData.class);
			if(pd == null) throw new RuntimeException("Server sent us invalid playerdata?!");
			System.out.println("Updating player " + pd.forUUID + "'s movement data on the client");
			Player pl = getPlayerWithUUID(pd.forUUID);
			pd.copyTo(pl);
		}
	}
	public Player getPlayerWithUUID(long uuid) {
		return getPlayerWithUUID(uuid, false);
	}
	public Player getPlayerWithUUID(long uuid, boolean includeLocal) {
		if(includeLocal && uuid == localPlayer.uuid)
			return localPlayer;
		for(PhysicsObject po : physicsObjects)
			if(po instanceof Player)
				if(((Player) po).uuid == uuid)
					return (Player) po;
		return null;
	}

	public void onDisconnect(SocketWrapperThread cw) {
		System.out.println("Disconnected from server!");
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		
	}
	public void onReceiveUnknownPacket(SocketWrapperThread clientWrapper, Packet p) {
		System.out.println("Got an unknown packet???!!!");
	}
	public void getPlayerList() {
		scomm.sendPacket(Packet.createRequest(EPacketIdentity.PLAYER_LIST));
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
	@Override
	public void add(Object p) {
		if(p instanceof ISocketListener)
			scomm.addClientListener((ISocketListener) p);
		super.add(p);
	}
	@Override
	public void remove(Object p) {
		if(p instanceof ISocketListener)
			scomm.removeClientListener((ISocketListener) p);
		super.remove(p);
	}

	public ITextStatistic getNetworkStatistics() {
		return new ITextStatistic() {
				public int getLevel() { return 1; }
				public String getHeader() { return "Network"; }
				public String[] getStatisticText() {
				return new String[] {
						"My UUID: " + localPlayer.uuid,
						"My name: " + localPlayer.name
				};
			}
		};
	}
}

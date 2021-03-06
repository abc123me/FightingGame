package jeremiahlowe.fightinggame.client;

import java.net.*;
import java.io.*;
import javax.swing.*;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.ins.*;
import jeremiahlowe.fightinggame.net.*;
import jeremiahlowe.fightinggame.net.sockets.*;
import jeremiahlowe.fightinggame.net.struct.*;
import jeremiahlowe.fightinggame.phys.*;
import jeremiahlowe.fightinggame.ui.IStatistic.*;
import processing.core.PApplet;

public class GameClientInstance extends GraphicalInstance implements ISocketListener {
	private SocketWrapperThread scomm;
	private boolean connected = false;
	
	public Player localPlayer;
	
	public GameClientInstance(PApplet applet) {
		super(applet);
	}
	
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
	public void sendVersionData() {
		scomm.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
	}
	public void disconnect() {
		connected = false;
		scomm.close();
	}
	public boolean isConnected() {
		return connected;
	}
	public void onConnect(SocketWrapperThread cw) {
		System.out.println("Sucesfully connected to server!");
		connected = true;
	}
	/**
	 * 
	 * ISocketListener
	 * 
	 */
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.VERSION_DATA)
			scomm.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.PLAYER_ADD) {
			Player pl = Meta.gson.fromJson(p.contents, Player.class);
			if(pl == null) throw new RuntimeException("Server sent us invalid playerdata?!");
			pl.ignoreKeys = false;
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
		else if(p.identity == EPacketIdentity.NAME_UPDATE) {
			NameChange nc = NameChange.fromJSON(p.contents);
			if(nc != null) {
				Player pl = getPlayerWithUUID(nc.uuid);
				if(pl != null)
					pl.name = nc.name;
			}
		}
		else if(p.identity == EPacketIdentity.CLIENT_KICK) {
			JOptionPane.showMessageDialog(this.applet.frame, p.contents, "You were kicked!", JOptionPane.INFORMATION_MESSAGE);
			System.exit(FightingGameClient.KICKED_EXITCODE);
		}
		else if(p.identity == EPacketIdentity.PLAYER_REMOVE) {
			long uuid = Long.parseLong(p.contents);
			System.out.println("Removing player based off of UUID: " + uuid);
			Player pl = getPlayerWithUUID(uuid);
			if(pl == null) System.out.println("No player with UUID: " + uuid);
			else remove(pl);
		}
		else if(p.identity == EPacketIdentity.PLAYER_MOVEMENT) {
			MovementData pd = Meta.gson.fromJson(p.contents, MovementData.class);
			if(pd == null) throw new RuntimeException("Server sent us invalid player movement packet?!");
			Player pl = getPlayerWithUUID(pd.forUUID);
			if(pl != null) updatePlayerMovementData(pd, pl);
			else throw new RuntimeException("Server sent us invalid player movement packet?!");
		}
		else if(p.identity == EPacketIdentity.ATTACK_UPDATE) {
			AttackData ad = Meta.gson.fromJson(p.contents, AttackData.class);
			if(ad == null) throw new RuntimeException("Server sent us invalid attackdata!");
			Player victim = getPlayerWithUUID(ad.getVictimUUID());
			String attackerName = "Unknown", victimName = "Unknown";
			Player attacker = getPlayerWithUUID(ad.getAttackerUUID());
			if(attacker != null) 
				attackerName = attacker.name;
			if(victim != null) 
				victimName = victim.name;
			ad.copyTo(attacker, victim);
			System.out.println("Got AttackData: " + ad.toString(attackerName, victimName));
			System.out.println("Attacker, Victim health: " + ad.getAttackerHealth() + ", " + ad.getVictimHealth());
		}
		else if(p.identity == EPacketIdentity.PLAYER_POSITION) {
			PositionData pd = Meta.gson.fromJson(p.contents, PositionData.class);
			if(pd != null) {
				Player pl = getPlayerWithUUID(pd.forUUID);
				if(pl != null) pd.copyTo(pl);
			}
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		System.out.println("Disconnected from server!");
		connected = false;
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		
	}
	public void onReceiveUnknownPacket(SocketWrapperThread clientWrapper, Packet p) {
		System.out.println("Got an unknown packet???!!!");
	}
	/**
	 * 
	 * Player management
	 * 
	 */
	public void updatePlayerMovementData(MovementData md, Player p) {
		boolean keys = false;
		boolean pos = true;
		md.copyTo(p, pos, keys);
	}
	public Player getPlayerWithUUID(long uuid) {
		return getPlayerWithUUID(uuid, true);
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
	public void getPlayerList() {
		scomm.sendPacket(Packet.createRequest(EPacketIdentity.PLAYER_LIST));
	}
	public Player getLocalPlayerFromServer() {
		scomm.sendPacket(Packet.createRequest(EPacketIdentity.CLIENT_PLAYER_DATA));
		Packet p = scomm.waitForUpdatePacket(1000, EPacketIdentity.CLIENT_PLAYER_DATA);
		if(p == null)
			return null;
		return Meta.gson.fromJson(p.contents, Player.class);
	}
	public void requestPositions() {
		scomm.sendPacket(Packet.createRequest(EPacketIdentity.PLAYER_POSITIONS));
	}
	public void updateLocalPlayer() {
		scomm.sendPacket(
				Packet.createUpdate(EPacketIdentity.PLAYER_MOVEMENT, //Packet type
				Meta.gson.toJson(new MovementData(localPlayer)))); //Movement data
	}
	public ITextStatistic getNetworkStatistics() {
		return new ITextStatistic() {
			public int getLevel() { return 1; }
			public String getHeader() { return "Network"; }
			public String[] getStatisticText() {
				return new String[] {
					"My UUID: " + localPlayer.uuid,
					"My name: " + localPlayer.name,
					"isConnected(): " + isConnected(),
					"Ping (Rx, Tx): " + scomm.getRxTime() + ", " + scomm.getTxTime(),
					"Packets waiting: " + scomm.getPacketsWaiting()
				};
			}
		};
	}
	public void updateLocalplayerName(String name) {
		scomm.sendPacket(Packet.createUpdate(EPacketIdentity.CLIENT_NAME, name));
	}

	/**
	 * 
	 * Raw networking
	 * 
	 */
	public void sendRawPacket(Packet p) {
		scomm.sendPacket(p);
	}
	public void setNetworkLag(int amount) {
		if(amount > 0)
			scomm.setNetworkLag(amount);
	}
	
	/**
	 * 
	 * Instance Superclass
	 * 
	 */
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
}

package jeremiahlowe.fightinggame.server;

import java.util.ArrayList;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.PlayerMovementData;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Color;
import net.net16.jeremiahlowe.shared.Timing;

public class ServerInstance extends Instance implements ISocketListener{
	private static final Gson gson = new Gson();
	private ArrayList<RemotePlayer> players;
	
	public ServerChatManager scm;
	public Server server;
	
	public ServerInstance(Server server) {
		this(server, false);
	}
	public ServerInstance(Server server, boolean add) {
		super();
		this.server = server;
		players = new ArrayList<RemotePlayer>();
		scm = new ServerChatManager(this);
		if(add)
			addSocketListeners(server);
	}

	public void addSocketListeners(Server s) {
		s.addClientListener(this);
		s.addClientListener(scm);
	}
	public Player[] getPlayerList() {
		int inc = 0;
		Player[] out = new Player[players.size()];
		for(RemotePlayer p : players) {
			if(p == null)
				continue;
			out[inc] = p.p; 
			inc++;
		}
		if(inc != out.length) {
			Player[] n = new Player[inc];
			for(int i = 0; i < out.length; i++)
				n[i] = out[i];
			return n;
		} else return out;
	}
	public Player getPlayerWithUUID(long UUID) {
		for(RemotePlayer p : players)
			if(p.cw.UUID == UUID)
				return p.p;
		return null;
	}
	public SocketWrapperThread getWrapperWithUUID(long UUID) {
		for(RemotePlayer p : players)
			if(p.cw.UUID == UUID)
				return p.cw;
		return null;
	}
	public Player createPlayer(long uuid) {
		Player p = new Player(uuid);
		p.color = new Color(255, 0, 0);
		p.health = 90;
		p.maxHealth = 100;
		p.alive = true;
		p.invincible = false;
		return p;
	}
	public void addPlayer(RemotePlayer remote) {
		players.add(remote);
		add(remote.p);
		server.broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, gson.toJson(remote.p)));
	}
	public void addPlayerIgnoreSelf(RemotePlayer remote) {
		players.add(remote);
		add(remote.p);
		String json = gson.toJson(remote.p);
		server.broadcastAllBut(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, json), remote.cw.UUID);//, remote.cw.UUID);
	}
	public void removePlayer(RemotePlayer remote) {
		players.remove(remote);
		remove(remote.p);
		server.broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_REMOVE, String.valueOf(remote.p.uuid)));
	}
	public void updatePlayerMovementData(PlayerMovementData pmd) {
		String json = gson.toJson(pmd);
		server.broadcastAllBut(Packet.createUpdate(EPacketIdentity.PLAYER_MOVEMENT, json), pmd.forUUID);
	}
	public void removePlayerWithUUID(long uuid) {
		RemotePlayer toRemove = null;
		for(RemotePlayer p : players)
			if(p.cw.UUID == uuid)
				toRemove = p;
		if(toRemove == null)
			return;
		removePlayer(toRemove);
	}
	public void kickPlayerWithUUID(long uuid, String reason) {
		SocketWrapperThread w = getWrapperWithUUID(uuid);
		if(w == null) {
			System.out.println("No player with UUID: " + uuid);
			return;
		}
		w.interrupt();
		w.sendPacket(Packet.createUpdate(EPacketIdentity.CLIENT_KICK, reason));
		System.out.println("Kicked player with UUID: " + uuid);
		removePlayerWithUUID(uuid);
	}
	private void getClientVersionData(SocketWrapperThread cw) {
		Thread verThread = new Thread() {
			@Override
			public void run() {
				Timing t = new Timing();
				Packet p = cw.waitForUpdatePacket(1000, EPacketIdentity.VERSION_DATA);
				Logger.log("Got version data: " + p + " (took " + t.millis() + "ms)", 2);
				if(p == null)
					cw.queueDisconnect();
				else {
					long cver = Long.parseLong(p.contents);
					if(cver != Meta.VERSION_ID) {
						Logger.log("Client tried to join with version " + cver + " but server is using version " + Meta.VERSION_ID, 1);
						cw.queueDisconnect();
					}
				}
			}
		};
		verThread.start();
	}

	@Override 
	public void onConnect(SocketWrapperThread cw) {
		getClientVersionData(cw);
	}
	@Override 
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.PLAYER_MOVEMENT) {
			PlayerMovementData pmd = gson.fromJson(p.contents, PlayerMovementData.class);
			Player pl = getPlayerWithUUID(cw.UUID);
			if(pl == null) {
				Logger.log("Got playerdata for a nonexistant player, Killing it now!", 1);
				if(cw.isAlive()) {
					try{
						cw.interrupt();
						Logger.log("GG rest in spagetti @ " + cw.UUID, 2);
					} catch(Exception e) {}
				}
				return;
			}
			pmd.copyTo(pl);
			updatePlayerMovementData(pmd);
			Logger.log("Updated player movement data", 4);
		}
	}
	@Override 
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.VERSION_DATA) {
			Logger.log("Client requested version sending it to him now", 2);
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
		}
		else if(p.identity == EPacketIdentity.CLIENT_PLAYER_DATA) {
			Logger.log("Client requested their player data sending it to him now", 2);
			Player player = getPlayerWithUUID(cw.UUID);
			if(player == null) {
				player = createPlayer(cw.UUID);
				addPlayerIgnoreSelf(new RemotePlayer(player, cw));
			}
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.CLIENT_PLAYER_DATA, gson.toJson(player)));
		}
		else if(p.identity == EPacketIdentity.PLAYER_LIST) {
			Logger.log("Client requested the player list, sending it to him", 2);
			for(Player pl : getPlayerList()) 
				cw.sendPacket(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, gson.toJson(pl)));
		}
	}
	@Override 
	public void onDisconnect(SocketWrapperThread cw) {
		removePlayerWithUUID(cw.UUID);
	}
	@Override public void onReceiveData(SocketWrapperThread cw, String data) {}
	@Override public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
}

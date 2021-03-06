package jeremiahlowe.fightinggame.server;

import java.util.ArrayList;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.net.*;
import jeremiahlowe.fightinggame.net.sockets.*;
import jeremiahlowe.fightinggame.net.struct.*;
import jeremiahlowe.fightinggame.phys.*;
import net.net16.jeremiahlowe.shared.*;
import net.net16.jeremiahlowe.shared.math.Vector;

public class ServerInstance extends Instance implements ISocketListener, IDamageListener{
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
 
	/**
	 * 
	 * ISocketListener
	 * 
	 */
	public void onConnect(SocketWrapperThread cw) {
		getClientVersionData(cw);
	}  
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.PLAYER_MOVEMENT) {
			MovementData pmd = Meta.gson.fromJson(p.contents, MovementData.class);
			Player pl = getPlayerWithUUID(cw.UUID);
			if(pl == null) {
				Logger.log("Got playerdata for a nonexistant player, Killing it now!", 1);
				cw.killCommunications();
				return;
			} else {
				updatePlayerMovementData(pmd, pl);
				Logger.log("Updated player movement data", 4);
			}
		}
		else if(p.identity == EPacketIdentity.CLIENT_NAME) {
			Player pl = getPlayerWithUUID(cw.UUID);
			String name = "";
			for(int i = 0; i < p.contents.length(); i++) {
				char c = p.contents.charAt(i);
				if(c >= ' ' && c <= '~')
					name += c;
			}
			if(name.length() > Player.MAX_NAME_LENGTH)
				name = name.substring(Player.MAX_NAME_LENGTH);
			if(pl != null)
				pl.name = name;
			NameChange nc = new NameChange(name, cw.UUID);
			server.broadcastAllBut(nc.createPacket(), cw.UUID);
			Logger.log("Set client " + cw.UUID + "'s name to " + pl.name, 2);
		}
	} 
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
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.CLIENT_PLAYER_DATA, Meta.gson.toJson(player)));
		}
		else if(p.identity == EPacketIdentity.PLAYER_LIST) {
			Logger.log("Client requested the player list, sending it to him", 2);
			for(Player pl : getPlayerList()) 
				cw.sendPacket(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, Meta.gson.toJson(pl)));
		}
		else if(p.identity == EPacketIdentity.PLAYER_POSITIONS) {
			for(RemotePlayer pl : players)
				cw.sendPacket(Packet.createUpdate(EPacketIdentity.PLAYER_POSITION, 
						Meta.gson.toJson(new PositionData(pl.p))));
		}
	} 
	public void onDisconnect(SocketWrapperThread cw) {
		removePlayerWithUUID(cw.UUID);
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
	/**
	 * 
	 * IDamageListener
	 * 
	 */
	public void onDeath(Instance i, Object killer, DamageableFighter victim) { 
		if(victim instanceof Player) {
			Player p = (Player) victim;
			AttackData toData = AttackData.createMurder(null, p);
			if(killer instanceof Player)
				toData.setAttacker((Player) killer);
			broadcast(toData.toPacket());
		}
	}
	public void onTakeDamage(Instance i, Object from, DamageableFighter to, float amount) {
		if(to instanceof Player) {
			Player p = (Player) to;
			AttackData toData = AttackData.createDamage(null, p);
			Player f = null;
			if(from instanceof Player)
				f = (Player) from;
			else if(from instanceof Bullet) {
				Fighter par = ((Bullet) from).getParent();
				if(par instanceof Player)
					f = (Player) par;
			}
			toData.setAttacker((Player) f);
			broadcast(toData.toPacket());
		}
	}
	/**
	 * 
	 * Player management
	 * 
	 */
	public void updatePlayerMovementData(MovementData md, Player p) {
		md.copyTo(p, false, true);
		broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_MOVEMENT, Meta.gson.toJson(new MovementData(p))));
	}
	public Player getPlayerWithName(String name) {
		if(name == null)
			return null;
		for(RemotePlayer p : players) 
			if(p != null && p.p != null && name.contentEquals(p.p.name))
				return p.p;
		return null;
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
		p.ignoreKeys = false;
		return p;
	}
	public void addPlayerListeners(Player p) {
		p.addDamageListener(this);
	}
	public void removePlayerListeners(Player p) {
		p.removeDamageListener(this);
	}
	public void addPlayer(RemotePlayer remote) {
		addPlayerListeners(remote.p);
		players.add(remote);
		add(remote.p);
		broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, Meta.gson.toJson(remote.p)));
	}
	public void addPlayerIgnoreSelf(RemotePlayer remote) {
		addPlayerListeners(remote.p);
		players.add(remote);
		add(remote.p);
		String json = Meta.gson.toJson(remote.p);
		broadcastAllBut(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, json), remote.cw.UUID);//, remote.cw.UUID);
	}
	public void removePlayer(RemotePlayer remote) {
		removePlayerListeners(remote.p);
		players.remove(remote);
		remove(remote.p);
		broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_REMOVE, String.valueOf(remote.p.uuid)));
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
	private void getClientVersionData(SocketWrapperThread c) {
		final SocketWrapperThread cw = c;
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
	/**
	 * 
	 * Player position management
	 * 
	 */
	public void broadcastPlayerPosition(long uuid) { getPlayerWithUUID(uuid); }
	public void broadcastPlayerPosition(Player p) {
		Packet.createUpdate(EPacketIdentity.PLAYER_POSITION, Meta.gson.toJson(new PositionData(p)));
	}
	public void teleportPlayerTo(long uuid, Vector pos) { teleportPlayerTo(getPlayerWithUUID(uuid), pos); }
	public void teleportPlayerTo(Player p, Vector pos) { teleportPlayerTo(p, pos, true); }
	public void teleportPlayerTo(Player p, Vector pos, boolean bcast) {
		p.pos = pos.copy();
		if(bcast) broadcastPlayerPosition(p);
	}
	/**
	 * 
	 * Socket stuff
	 * 
	 */
	public void addSocketListeners(Server s) {
		s.addClientListener(this);
		s.addClientListener(scm);
	}
	/**
	 * 
	 * Raw packet sending
	 * 
	 */
	public void broadcast(Packet packet) {
		server.broadcast(packet);
	}
	public void broadcastAllBut(Packet packet, long uuid) {
		server.broadcastAllBut(packet, uuid);
	}
	public void sendPacket(Packet packet, long toUUID) {
		SocketWrapperThread w = getWrapperWithUUID(toUUID);
		w.sendPacket(packet);
	}
}

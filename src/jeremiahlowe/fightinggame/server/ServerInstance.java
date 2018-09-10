package jeremiahlowe.fightinggame.server;

import java.util.ArrayList;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.PlayerMovementData;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Color;

public class ServerInstance extends Instance{
	private static final Gson gson = new Gson();
	private ArrayList<RemotePlayer> players;
	
	public Server server;
	
	public ServerInstance(Server server) {
		super();
		this.server = server;
		players = new ArrayList<RemotePlayer>();
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
		String json = gson.toJson(remote.p);
		server.broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_REMOVE, json));//, remote.cw.UUID);
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
	public void kickPlayerWithUUID(long uuid) {
		SocketWrapperThread w = getWrapperWithUUID(uuid);
		if(w == null) {
			System.out.println("No player with UUID: " + uuid);
			return;
		}
		w.interrupt();
		System.out.println("Kicked player with UUID: " + uuid);
		removePlayerWithUUID(uuid);
	}
}

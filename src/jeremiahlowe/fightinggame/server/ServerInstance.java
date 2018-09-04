package jeremiahlowe.fightinggame.server;

import java.util.ArrayList;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
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

	public Player getPlayerWithUUID(long UUID) {
		for(RemotePlayer p : players)
			if(p.cw.UUID == UUID)
				return p.p;
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
		server.broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, gson.toJson(remote.p)));
	}
	public void addPlayerIgnoreSelf(RemotePlayer remote) {
		players.add(remote);
		server.broadcastAllBut(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, gson.toJson(remote.p)), remote.cw.UUID);
	}
	public void removePlayer(RemotePlayer remote) {
		players.remove(remote);
		server.broadcast(Packet.createUpdate(EPacketIdentity.PLAYER_REMOVE, gson.toJson(remote.p)));
	}
}

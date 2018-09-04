package jeremiahlowe.fightinggame.server;

import java.util.ArrayList;

import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Color;

public class ServerInstance extends Instance{
	private ArrayList<RemotePlayer> players;
	
	public ServerInstance() {
		super();
		players = new ArrayList<RemotePlayer>();
	}

	public Player getPlayerWithUUID(long UUID) {
		for(RemotePlayer p : players)
			if(p.cw.UUID == UUID)
				return p.p;
		return null;
	}
	public Player createPlayer() {
		Player p = new Player(this);
		p.color = new Color(255, 0, 0);
		p.health = 90;
		p.maxHealth = 100;
		p.alive = true;
		p.invincible = false;
		return p;
	}

	public void addPlayer(RemotePlayer remote) {
		players.add(remote);
	}
	public void removePlayer(RemotePlayer remote) {
		players.remove(remote);
	}
}

package jeremiahlowe.fightinggame.server;

import jeremiahlowe.fightinggame.phys.Player;

public class RemotePlayer {
	public final ClientWrapper cw;
	public final Player p;
	
	public RemotePlayer(Player p, ClientWrapper cw) {
		this.p = p;
		this.cw = cw;
	}
}

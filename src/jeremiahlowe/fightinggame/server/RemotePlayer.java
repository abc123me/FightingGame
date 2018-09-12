package jeremiahlowe.fightinggame.server;

import jeremiahlowe.fightinggame.net.sockets.SocketWrapperThread;
import jeremiahlowe.fightinggame.phys.Player;

public class RemotePlayer {
	public final SocketWrapperThread cw;
	public final Player p;
	
	public RemotePlayer(Player p, SocketWrapperThread cw) {
		this.p = p;
		this.cw = cw;
	}
}

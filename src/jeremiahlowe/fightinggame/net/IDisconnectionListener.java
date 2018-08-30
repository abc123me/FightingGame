package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.ClientWrapper;

public interface IDisconnectionListener {
	public void onDisconnect(ClientWrapper cw);
}

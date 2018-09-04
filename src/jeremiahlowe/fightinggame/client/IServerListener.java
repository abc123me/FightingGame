package jeremiahlowe.fightinggame.client;

import jeremiahlowe.fightinggame.net.SocketCommunicator;

public interface IServerListener {
	public void onServerRequest(SocketCommunicator server);
}

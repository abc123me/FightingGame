package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.ClientWrapper;

public interface IDataListener {
	public void onReceiveData(ClientWrapper cw, String recieved);
}

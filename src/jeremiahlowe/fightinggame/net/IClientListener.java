package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.ClientWrapper;

public interface IClientListener {
	public void onConnect(ClientWrapper cw);
	public void onReceiveRequest(ClientWrapper cw, Packet p);
	public void onReceiveUpdate(ClientWrapper cw, Packet p);
	public void onDisconnect(ClientWrapper cw);
	public void onReceiveData(ClientWrapper cw, String data);
	public void onReceiveUnknownPacket(ClientWrapper clientWrapper, Packet p);
}

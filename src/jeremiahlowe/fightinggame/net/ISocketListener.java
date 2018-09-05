package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.SocketWrapperThread;

public interface ISocketListener {
	public void onConnect(SocketWrapperThread cw);
	public void onReceiveRequest(SocketWrapperThread cw, Packet p);
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p);
	public void onDisconnect(SocketWrapperThread cw);
	public void onReceiveData(SocketWrapperThread cw, String data);
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p);
}

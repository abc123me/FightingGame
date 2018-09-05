package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.SocketWrapperThread;

public class SocketAdapter implements ISocketListener {
	public void onConnect(SocketWrapperThread cw) {}
	public void onDisconnect(SocketWrapperThread cw) {}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {}
	public void onReceiveData(SocketWrapperThread cw, String data) {}
	public void onReceiveUnknownPacket(SocketWrapperThread clientWrapper, Packet p) {}
}

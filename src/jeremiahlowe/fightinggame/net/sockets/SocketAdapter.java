package jeremiahlowe.fightinggame.net.sockets;

import jeremiahlowe.fightinggame.net.Packet;

public class SocketAdapter implements ISocketListener {
	public void onConnect(SocketWrapperThread cw) {}
	public void onDisconnect(SocketWrapperThread cw) {}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {}
	public void onReceiveData(SocketWrapperThread cw, String data) {}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
}

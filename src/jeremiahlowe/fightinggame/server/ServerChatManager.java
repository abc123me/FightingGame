package jeremiahlowe.fightinggame.server;

import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.sockets.ISocketListener;
import jeremiahlowe.fightinggame.net.sockets.SocketWrapperThread;
import jeremiahlowe.fightinggame.net.struct.ChatMessage;

public class ServerChatManager implements ISocketListener{
	public final ServerInstance instance;
	
	public ServerChatManager(ServerInstance instance) {
		this.instance = instance;
	}
	
	public void serverSay(String msg) {
		ChatMessage sm = new ChatMessage(msg);
		instance.server.broadcast(sm.toPacket());
	}
	
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.CHAT_MESSAGE) {
			ChatMessage sm = new ChatMessage(p.contents, cw.UUID);
			instance.server.broadcastAllBut(sm.toPacket(), cw.UUID);
			Logger.log("Relayed chat message from " + cw.UUID + " with: " + p.contents, 3);
		}
	}
	public void onConnect(SocketWrapperThread cw) {}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	public void onDisconnect(SocketWrapperThread cw) {}
	public void onReceiveData(SocketWrapperThread cw, String data) {}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
}

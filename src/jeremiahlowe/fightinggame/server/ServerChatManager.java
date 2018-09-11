package jeremiahlowe.fightinggame.server;

import jeremiahlowe.fightinggame.net.ChatMessage;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;

public class ServerChatManager implements ISocketListener{
	public final ServerInstance instance;
	
	public ServerChatManager(ServerInstance instance) {
		this.instance = instance;
	}
	
	public void serverSay(String msg) {
		ChatMessage sm = new ChatMessage(msg);
		instance.server.broadcast(sm.toPacket());
	}
	
	@Override public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.CHAT_MESSAGE) {
			ChatMessage sm = new ChatMessage(p.contents, cw.UUID);
			instance.server.broadcastAllBut(sm.toPacket(), cw.UUID);
			Logger.log("Relayed chat message from " + cw.UUID + " with: " + p.contents, 3);
		}
	}
	@Override public void onConnect(SocketWrapperThread cw) {}
	@Override public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	@Override public void onDisconnect(SocketWrapperThread cw) {}
	@Override public void onReceiveData(SocketWrapperThread cw, String data) {}
	@Override public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
}

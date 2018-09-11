package jeremiahlowe.fightinggame.client.chat;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.client.GameClientInstance;
import jeremiahlowe.fightinggame.net.ChatMessage;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.phys.Player;
import jeremiahlowe.fightinggame.server.SocketWrapperThread;

public class RemoteChatManager implements ISocketListener, IChatListener {
	public final GameClientInstance gci;
	public final Chat chat;
	
	private final Gson gson = new Gson();
	
	public RemoteChatManager(GameClientInstance gci, Chat chat) {
		this.gci = gci;
		this.chat = chat;
	}
	
	@Override public void onSendMessage(String message) {
		gci.sendRawPacket(Packet.createUpdate(EPacketIdentity.CHAT_MESSAGE, message));
	}
	@Override public void onConnect(SocketWrapperThread cw) {
		chat.pushMessage("Connected to the server", "Client");
	}
	@Override public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	@Override public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.CHAT_MESSAGE) {
			System.out.println("CHAT MESSAGE");
			ChatMessage cm = gson.fromJson(p.contents, ChatMessage.class);
			String from = null;
			if(cm.isFromServer) 
				from = "Server";
			else {
				Player pl = gci.getPlayerWithUUID(cm.fromUUID);
				if(pl != null) from = pl.name;
			}
			if(from == null) {
				System.out.println("Origin of ChatMessage is null!");
				from = "null";
			}
			chat.pushMessage(cm.message, from);
		}
	}
	@Override public void onDisconnect(SocketWrapperThread cw) {
		chat.pushMessage("Disconnected from the server", "Client");
	}
	@Override public void onReceiveData(SocketWrapperThread cw, String data) {}
	@Override public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		chat.pushMessage("Unknown packet recieved", "Client");
	}
}

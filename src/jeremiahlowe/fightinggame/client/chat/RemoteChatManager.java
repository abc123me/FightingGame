package jeremiahlowe.fightinggame.client.chat;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.client.GameClientInstance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.sockets.ISocketListener;
import jeremiahlowe.fightinggame.net.sockets.SocketWrapperThread;
import jeremiahlowe.fightinggame.net.struct.ChatMessage;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Color;

public class RemoteChatManager implements ISocketListener, IChatListener {
	public final GameClientInstance gci;
	public final Chat chat;
	
	private final Gson gson = new Gson();
	
	public RemoteChatManager(GameClientInstance gci, Chat chat) {
		this.gci = gci;
		this.chat = chat;
	}
	
	public void onSendMessage(String message) {
		gci.sendRawPacket(Packet.createUpdate(EPacketIdentity.CHAT_MESSAGE, message));
	}
	public void onConnect(SocketWrapperThread cw) {
		chat.pushMessage("Connected to the server", "Client");
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.CHAT_MESSAGE) {
			System.out.println("CHAT MESSAGE");
			ChatMessage cm = gson.fromJson(p.contents, ChatMessage.class);
			String from = null;
			Color c = Color.BLACK;
			if(cm.isFromServer) {
				from = "Server";
				c = Color.RED;
			}
			else {
				Player pl = gci.getPlayerWithUUID(cm.fromUUID);
				if(pl != null) from = pl.name;
			}
			if(from == null) {
				System.out.println("Origin of ChatMessage is null!");
				from = "null";
			}
			chat.pushMessage(cm.message, from, c);
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		chat.pushMessage("Disconnected from the server", "Client");
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		chat.pushMessage("Unknown packet recieved", "Client");
	}
}

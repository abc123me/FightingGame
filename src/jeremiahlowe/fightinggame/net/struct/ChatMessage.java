package jeremiahlowe.fightinggame.net.struct;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;

public class ChatMessage {
	private static final Gson gson = new Gson();
	
	public String message;
	public long fromUUID;
	public boolean isFromServer;
	
	public ChatMessage(String message, long fromUUID) {
		this.message = message;
		this.fromUUID = fromUUID;
		this.isFromServer = false;
	}
	public ChatMessage(String message) {
		this.message = message;
		this.fromUUID = 0;
		this.isFromServer = true;
	}
	
	public Packet toPacket() {
		return Packet.createUpdate(EPacketIdentity.CHAT_MESSAGE, gson.toJson(this));
	}
	public static ChatMessage fromJSON(String s) {
		return gson.fromJson(s, ChatMessage.class);
	}
}

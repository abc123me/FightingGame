package jeremiahlowe.fightinggame.net;

import com.google.gson.Gson;

public class NameChange {
	private static final Gson gson = new Gson();
	
	public final String name;
	public final long uuid;
	
	public NameChange(String name, long uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	public Packet createPacket() {
		return Packet.createUpdate(EPacketIdentity.NAME_UPDATE, gson.toJson(this));
	}
	public static NameChange fromJSON(String json) {
		return gson.fromJson(json, NameChange.class);
	}
}

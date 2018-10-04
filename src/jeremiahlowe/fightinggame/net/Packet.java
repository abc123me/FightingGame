package jeremiahlowe.fightinggame.net;

import com.google.gson.Gson;

public class Packet {
	private static Gson gson = new Gson();
	
	public static final int INVALID = -1;
	public static final int REQUEST = 0;
	public static final int UPDATE = 1;
	public static final int CONNECTION_CHECK = 2;
	
	public final int type;
	public final EPacketIdentity identity;
	public final String contents;
	
	private Packet() {this(-1, null, null);}
	private Packet(int type, EPacketIdentity identity, String contents) {
		this.type = type;
		this.identity = identity;
		this.contents = contents;
	}
	
	public static Packet createRequest(EPacketIdentity identity) {
		return new Packet(REQUEST, identity, null);
	}
	public static Packet createUpdate(EPacketIdentity identity, String content) {
		return new Packet(UPDATE, identity, content);
	}
	public static Packet createConnCheck() {
		return new Packet(CONNECTION_CHECK, null, null);
	}
	public Packet copy() {
		return new Packet(type, identity, contents);
	}
	
	@Override
	public String toString() {
		String t = String.valueOf(type);
		if(type == REQUEST) t = "REQ";
		if(type == UPDATE) t = "UPD";
		if(type == INVALID) t = "INV";
		return "Packet{type=" + t + ", ident=" + identity + ", contents=" + contents + "}";
	}
	public String toJSON() {
		return gson.toJson(this);
	}
	public static Packet fromJSON(String json) {
		return gson.fromJson(json, Packet.class);
	}
}

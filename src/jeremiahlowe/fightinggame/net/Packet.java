package jeremiahlowe.fightinggame.net;

public class Packet {
	public static final int INVALID = -1;
	public static final int REQUEST = 0;
	public static final int UPDATE = 1;
	
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
}
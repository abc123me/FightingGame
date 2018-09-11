package jeremiahlowe.fightinggame;

public class Meta {
	public static final String VERSION = "V0.2";
	public static final long VERSION_ID;
	static{
		long calc = 0;
		for(int i = 0; i < VERSION.length(); i++) 
			calc += VERSION.charAt(i);
		VERSION_ID = calc; //Just do a checksum
	}
	
	private static boolean serversideSet = false;
	private static boolean serverside;
	
	public static void setServerside(boolean serverside) {
		if(serversideSet)
			return;
		Meta.serverside = serverside;
		serversideSet = true;
	}
	public static boolean serverside() {
		return serverside;
	}
}

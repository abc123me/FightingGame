package jeremiahlowe.fightinggame;

public class Meta {
	public static final int VERSION_ID = 1;
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

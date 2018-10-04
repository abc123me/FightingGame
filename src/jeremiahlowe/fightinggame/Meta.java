package jeremiahlowe.fightinggame;

import com.google.gson.Gson;

import net.net16.jeremiahlowe.shared.math.GeneralMath;

public class Meta {
	public static final Gson gson;
	public static final String VERSION = "V0.321";
	public static final long VERSION_ID;
	public static final String[] names = new String[] {
		"Bob", "Jeff", "Chuck", "Kevin", "Jeremiah", "Danielle",
		"Ben", "Peter", "The NSA", "Mike", "Tim", "Tom", "Bill"
	};
	static{
		long calc = 0;
		for(int i = 0; i < VERSION.length(); i++) 
			calc += VERSION.charAt(i);
		VERSION_ID = calc; //Just do a checksum
		gson = new Gson();
	}
	
	private static boolean serversideSet = false;
	private static boolean serverside;
	
	public static String getRandomName() {
		int i = (int) GeneralMath.random(0, names.length - 1);
		return names[i];
	}
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

package jeremiahlowe.fightinggame.server;

public class Logger {
	public static int level = 0;
	
	public static void log(String msg) {
		log(msg, 1);
	}
	public static void log(String msg, int level) {
		if(Logger.level >= level)
			System.out.println(msg);
	}
}

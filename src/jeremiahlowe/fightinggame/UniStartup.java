package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.client.FightingGameClient;
import jeremiahlowe.fightinggame.client.launcher.Launcher;
import jeremiahlowe.fightinggame.server.FightingGameServerCLI;

public class UniStartup {
	private static final int SERVER_MODE = 0;
	private static final int TEST_CLIENT_MODE = 1;
	private static final int LAUNCHER_MODE = 2;
	private static final int SINGLEPLAYER_MODE = 3;
	
	public static void main(String[] args) {
		int mode = LAUNCHER_MODE;
		for(String arg : args) {
			if("--server".equalsIgnoreCase(arg))
				mode = SERVER_MODE;
			if("--launcher".equalsIgnoreCase(arg))
				mode = LAUNCHER_MODE;
			if("--client".equalsIgnoreCase(arg))
				mode = TEST_CLIENT_MODE;
			if("--ssp".equalsIgnoreCase(arg))
				mode = SINGLEPLAYER_MODE;
		}
		switch(mode) {
			case LAUNCHER_MODE: Launcher.main(args); break;
			case TEST_CLIENT_MODE: FightingGameClient.main(args); break;
			case SERVER_MODE: FightingGameServerCLI.main(args); break;
			case SINGLEPLAYER_MODE: FightingGame.main(args); break;
			default: Launcher.main(args); break;
		}
	}
}

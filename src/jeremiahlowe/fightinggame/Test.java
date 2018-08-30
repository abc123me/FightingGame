package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.client.FightingGameClient;
import jeremiahlowe.fightinggame.server.FightingGameServerCLI;

public class Test {
	public static void main(String[] args) throws Exception{
		final String[] cargs = {""};
		Thread client = new Thread() {
			@Override
			public void run() {
				FightingGameClient.main(cargs);
			}
		};
		Thread server = new Thread() {
			@Override
			public void run() {
				FightingGameServerCLI s = new FightingGameServerCLI(1234);
				s.start();
			}
		};
		server.start();
		Thread.sleep(500);
		client.start();
		server.join();
	}
}

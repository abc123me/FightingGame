package jeremiahlowe.fightinggame.server;

import java.util.Scanner;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.PlayerMovementData;
import jeremiahlowe.fightinggame.phys.PhysicsObject;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Timing;

public class FightingGameServerCLI implements ISocketListener{
	private ServerInstance instance;
	private static final Gson gson = new Gson();
	private Thread userInputThread;
	private int debugLevel = 1;
	private double tps = 0, maxTPS = 30;
	private boolean lagg = false;
	
	public static void main(String[] args) {
		Meta.setServerside(true);
		FightingGameServerCLI cli = new FightingGameServerCLI(1234);
		cli.start();
	}
	
	public FightingGameServerCLI(int port) {
		Server server = new Server(port);
		server.addClientListener(this);
		instance = new ServerInstance(server);
	}
	
	public void start() {
		userInputThread = new Thread() {
			@Override public void run() { userInputLoop(); }
		};
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override public void run() { shutdownHook(); }
		});
		instance.server.start();
		userInputThread.start();
		physicsLoop();
	}
	
	private void physicsLoop() {
		Timing t = new Timing();
		double deltaTime = 0;
		while(true) {
			t.reset();
			instance.physicsUpdate(deltaTime);
			if(lagg) Timing.sleep(Math.round((Math.random() + 0.5) * 500));
			if(tps > maxTPS) {
				double waitFor = ((1.0 / maxTPS) - deltaTime) * 1000.0;
				if(!Timing.sleep(Math.round(waitFor)))
					break;
			}
			deltaTime = t.secs();
			tps = 1.0 / deltaTime; 
		}
	}
	private void shutdownHook() {
		instance.server.serverStop();
		if(userInputThread != null)
			userInputThread.interrupt();
		System.out.println("Bye from shutdownHook()");
	}
	private void userInputLoop() {
		Scanner in = new Scanner(System.in);
		while(!Thread.interrupted()) {
			System.out.print("> ");
			while(!in.hasNextLine())
				Timing.sleep(100);
			String input = in.nextLine();
			handleUserInput(input);
		}
		in.close();
	}
	private void printHelp() {
		System.out.println("Commands:");
		System.out.println("help: Shows this help screen");
		System.out.println("exit: Stops the server");
		System.out.println("list: Lists all players");
		System.out.println("kick <uuid>: Kicks a player");
		System.out.println("debug <level>: Sets the debug level (0-3)");
		System.out.println("tps: Gets the TPS the server is running at");
		System.out.println("lag: Lags the server");
		System.out.println("lsphys: Lists the physicsobjects the server is handling");
		System.out.println("kickall: Kicks all players");
	}
	public void handleUserInput(String input) {
		input = input.trim();
		if(input.equals("exit")) {
			System.out.println("Exiting now!");
			System.exit(0);
		}
		else if(input.equals("list")) {
			System.out.println("Player list (" + instance.getPlayerList().length + "):");
			for(Player p : instance.getPlayerList()) {
				if(p != null) System.out.println("\tPlayer: " + p.uuid + " with name " + p.name);
				else System.out.println("\tNull player!");
			}
		}
		else if(input.equals("help")) 
			printHelp();
		else if(input.startsWith("debug")) {
			String[] parts = input.split(" ");
			if(parts.length != 2) 
				System.out.println("Debug needs a debug level parameter!");
			else{
				try {
					debugLevel = Integer.parseInt(parts[1]);
					System.out.println("Set debug level to " + parts[1]);
				} catch(Exception e) { System.out.println("Invalid debug level \"" + parts[1] + "\""); }
			}
		}
		else if(input.equals("kickall")) 
			for(Player p : instance.getPlayerList())
				instance.kickPlayerWithUUID(p.uuid);
		else if(input.startsWith("kick")) {
			String[] parts = input.split(" ");
			long uuid = 0;
			if(parts.length != 2) {
				System.out.println("Debug needs a debug level parameter!");
				return;
			}
			try {
				uuid = Long.parseLong(parts[1]);
				instance.kickPlayerWithUUID(uuid);
			} catch(Exception e) { 
				System.out.println("Error during kicking player with UUID \"" + parts[1] + "\": " + e); 
			}
		}
		else if(input.equals("tps")) 
			System.out.println("TPS: " + tps);
		else if(input.equals("lag")) {
			lagg = !lagg;
			System.out.println("Server lag " + (lagg ? "en" : "dis") + "abled!");
		}
		else if(input.equals("lsphys")) {
			System.out.println("Physics objects:");
			for(PhysicsObject po : instance.getPhysicsObjects()) {
				if(po == null) continue;
				System.out.println("\tPhysicsObject: " + po);
				System.out.println("\t\tPosition: " + po.pos);
				System.out.println("\t\tVelocity: " + po.vel);
				System.out.println("\t\tEnabled: " + po.enabled());
			}
		}
		else System.out.println("Unknown command, type \"help\" for help!");
	}

	public void onConnect(SocketWrapperThread cw) {
		if(debugLevel > 0)
			System.out.println("Client " + cw.UUID + " connected!");
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		if(debugLevel >= 3)
			System.out.println("Client sent request for: " + p.identity);
		if(p.identity == EPacketIdentity.VERSION_DATA) {
			if(debugLevel >= 2)
				System.out.println("Client requested version sending it to him now");
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
		}
		else if(p.identity == EPacketIdentity.CLIENT_PLAYER_DATA) {
			if(debugLevel >= 2)
				System.out.println("Client requested their player data sending it to him now");
			Player player = instance.getPlayerWithUUID(cw.UUID);
			if(player == null) {
				player = instance.createPlayer(cw.UUID);
				instance.addPlayerIgnoreSelf(new RemotePlayer(player, cw));
			}
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.CLIENT_PLAYER_DATA, gson.toJson(player)));
		}
		else if(p.identity == EPacketIdentity.PLAYER_LIST) {
			if(debugLevel >= 2)
				System.out.println("Client requested the player list, sending it to him");
			for(Player pl : instance.getPlayerList()) 
				cw.sendPacket(Packet.createUpdate(EPacketIdentity.PLAYER_ADD, gson.toJson(pl)));
		}
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		if(debugLevel >= 3)
			System.out.println("Client sent update for: " + p.identity);
		if(p.identity == EPacketIdentity.PLAYER_MOVEMENT) {
			PlayerMovementData pmd = gson.fromJson(p.contents, PlayerMovementData.class);
			Player pl = instance.getPlayerWithUUID(cw.UUID);
			if(pl == null) {
				System.out.println("Got playerdata for a nonexistant player, Killing it now!");
				if(cw.isAlive()) {
					try{
						cw.interrupt();
						System.out.println("GG rest in spagetti @ " + cw.UUID);
					} catch(Exception e) {}
				}
				return;
			}
			pmd.copyTo(pl);
			instance.updatePlayerMovementData(pmd);
			if(debugLevel >= 4)
				System.out.println("Updated player movement data");
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		if(cw == null)
			return;
		if(debugLevel >= 1)
			System.out.println("Client " + cw.UUID + " disconnected!");
		instance.removePlayerWithUUID(cw.UUID);
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		
	}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		if(debugLevel >= 1)
			System.out.println("Got unknown packet!");
	}
}

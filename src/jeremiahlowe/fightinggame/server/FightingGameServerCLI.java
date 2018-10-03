package jeremiahlowe.fightinggame.server;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.sockets.ISocketListener;
import jeremiahlowe.fightinggame.net.sockets.SocketWrapperThread;
import jeremiahlowe.fightinggame.net.struct.HealthData;
import jeremiahlowe.fightinggame.net.struct.NameChange;
import jeremiahlowe.fightinggame.phys.DamageableFighter;
import jeremiahlowe.fightinggame.phys.IDamageListener;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Timing;

public class FightingGameServerCLI implements ISocketListener, IDamageListener{
	private ServerInstance instance;
	private InteractionThread userInputThread;
	private Thread physicsThread;
	private double tps = 0, maxTPS = 30;
	private boolean lagg = false;
	
	public static void main(String[] args) {
		Meta.setServerside(true);
		int port = 1234;
		for(int i = 1; i < args.length; i++) {
			String arg = args[i - 1];
			String next = args[i];
			if(arg.equals("-p") || arg.equals("--port"))
				port = Integer.parseInt(next);
		}
		System.out.println("Port set to " + port);
		FightingGameServerCLI cli = new FightingGameServerCLI(port);
		cli.start();
	}
	
	public FightingGameServerCLI(int port) {
		Server server = new Server(port);
		server.addClientListener(this);
		instance = new ServerInstance(server, true);
	}
	
	public void start() {
		userInputThread = new InteractionThread(instance, this);
		userInputThread.start();
		physicsThread = new Thread() {
			@Override public void run() { physicsLoop(); }
		};
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override public void run() { shutdownHook(); }
		});
		instance.server.start();
		physicsThread.start();
		try{instance.server.join();}
		catch(Exception e) {}
		userInputThread.interrupt();
		physicsThread.interrupt();
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

	public void onConnect(SocketWrapperThread cw) {
		Logger.log("Client " + cw.UUID + " connected!", 0);
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		Logger.log("Client sent request for: " + p.identity, 4);
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		Logger.log("Client sent update for: " + p.identity, 4);
		if(p.identity == EPacketIdentity.CLIENT_NAME) {
			Player pl = instance.getPlayerWithUUID(cw.UUID);
			String name = "";
			for(int i = 0; i < p.contents.length(); i++) {
				char c = p.contents.charAt(i);
				if(c >= ' ' && c <= '~')
					name += c;
			}
			if(name.length() > Player.MAX_NAME_LENGTH)
				name = name.substring(Player.MAX_NAME_LENGTH);
			if(pl != null)
				pl.name = name;
			NameChange nc = new NameChange(name, cw.UUID);
			instance.server.broadcastAllBut(nc.createPacket(), cw.UUID);
			Logger.log("Set client " + cw.UUID + "'s name to " + pl.name, 2);
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		if(cw == null)
			return;
		Logger.log("Client " + cw.UUID + " disconnected!", 1);
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		
	}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		Logger.log("Got unknown packet!", -1);
	}
	public boolean lagg() {
		return lagg;
	}
	public double tps() {
		return tps;
	}
	public void toggleLagg() {
		lagg = !lagg;
	}

	public void onDeath(Instance i, DamageableFighter from) { }
	public void onTakeDamage(Instance i, Object from, DamageableFighter to, float amount) {
		if(to instanceof Player) {
			Player p = (Player) to;
			HealthData toData = new HealthData(p);
			Packet pkt = toData.toPacket();
			instance.server.broadcast(pkt);
		}
	}
}

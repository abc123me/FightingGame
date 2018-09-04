package jeremiahlowe.fightinggame.server;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.net.IClientListener;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Timing;

public class FightingGameServerCLI implements IClientListener{
	private Server server;
	private ServerInstance instance;
	private Gson gson = new Gson();;
	
	public FightingGameServerCLI(int port) {
		server = new Server(port);
		server.addClientListener(this);
		instance = new ServerInstance();
	}
	
	public void start() {
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.serverStop();
			}
		});
		Timing t = new Timing();
		double deltaTime = 0;
		while(true) {
			t.reset();
			instance.physicsUpdate(deltaTime);
			deltaTime = t.secs();
		}
	}

	public void onConnect(SocketWrapperThread cw) {
		System.out.println("Client " + cw.UUID + " connected!");
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		if(p.identity == EPacketIdentity.VERSION_DATA) {
			System.out.println("Client requested version sending it to him now");
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.VERSION_DATA, String.valueOf(Meta.VERSION_ID)));
		}
		else if(p.identity == EPacketIdentity.PLAYER_DATA) {
			System.out.println("Client requested their player data sending it to him now");
			Player player = instance.getPlayerWithUUID(cw.UUID);
			if(player == null) {
				player = instance.createPlayer();
				instance.addPlayer(new RemotePlayer(player, cw));
			}
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.PLAYER_DATA, gson.toJson(player)));
		}
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		System.out.println("Got update packet!");
		if(p.identity == EPacketIdentity.PHYSICS_DATA) {
			
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		System.out.println("Client " + cw.UUID + " disconnected!");
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		System.out.println("Received data from client " + cw.UUID + ":");
		System.out.println(data);
	}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		System.out.println("Got unknown packet!");
	}
}

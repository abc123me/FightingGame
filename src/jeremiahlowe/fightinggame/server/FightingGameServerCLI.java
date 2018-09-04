package jeremiahlowe.fightinggame.server;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.PlayerMovementData;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.Timing;

public class FightingGameServerCLI implements ISocketListener{
	private ServerInstance instance;
	private static final Gson gson = new Gson();
	
	public static void main(String[] args) {
		FightingGameServerCLI cli = new FightingGameServerCLI(1234);
		cli.start();
	}
	
	public FightingGameServerCLI(int port) {
		Server server = new Server(port);
		server.addClientListener(this);
		instance = new ServerInstance(server);
	}
	
	public void start() {
		instance.server.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				instance.server.serverStop();
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
		else if(p.identity == EPacketIdentity.LOCAL_PLAYER_DATA) {
			System.out.println("Client requested their player data sending it to him now");
			Player player = instance.getPlayerWithUUID(cw.UUID);
			if(player == null) {
				player = instance.createPlayer(cw.UUID);
				instance.addPlayerIgnoreSelf(new RemotePlayer(player, cw));
			}
			cw.sendPacket(Packet.createUpdate(EPacketIdentity.LOCAL_PLAYER_DATA, gson.toJson(player)));
		}
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		//System.out.println("Got update packet!");
		if(p.identity == EPacketIdentity.LOCAL_PLAYER_MOVEMENT) {
			PlayerMovementData pmd = gson.fromJson(p.contents, PlayerMovementData.class);
			Player pl = instance.getPlayerWithUUID(cw.UUID);
			pmd.copyTo(pl);
			System.out.println("Updated player movement data");
		}
	}
	public void onDisconnect(SocketWrapperThread cw) {
		System.out.println("Client " + cw.UUID + " disconnected!");
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		//System.out.println("Received data from client " + cw.UUID + ":");
		//System.out.println(data);
	}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		System.out.println("Got unknown packet!");
	}
}

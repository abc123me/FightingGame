package jeremiahlowe.fightinggame.client;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.phys.Player;
import jeremiahlowe.fightinggame.server.SocketWrapperThread;
import net.net16.jeremiahlowe.shared.math.Vector;
import net.net16.jeremiahlowe.shared.math.Viewport;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class FightingGameClient extends PApplet implements ISocketListener{
	public static boolean DEBUG_MODE = false;

	public static void main(String[] args) {
		DEBUG_MODE = true;
		Meta.setServerside(false);
		main(FightingGameClient.class, args);
	}
	
	public float worldSize = 10;
	
	private GameClientInstance instance;
	private Player localPlayer;
	
	@Override
	public void settings() {
		size(500, 500);
		instance = new GameClientInstance(this);
		instance.screen = new Viewport(width, -height, width / 2, height / 2);
		instance.world = new Viewport(worldSize * instance.screen.aspRatio(), worldSize, 0, 0);
		if(!instance.connectToServer("localhost", 1234))
			System.exit(1);
		localPlayer = instance.getLocalPlayerFromServer();
		if(localPlayer == null) {
			System.err.println("Was unable to retrieve player from the server?!");
			System.exit(-1);
		}
		instance.localPlayer = localPlayer;
		instance.addAll(localPlayer, instance.getNetworkStatistics());
		instance.getPlayerList();
	}
	@Override
	public void setup() {
		if (DEBUG_MODE)
			instance.statistics.level = 9000;
		frameRate(60);
	}
	@Override
	public void draw() {
		background(255);
		instance.physicsUpdate((1 / frameRate));
		instance.drawAll(this);
	}
	
	@Override
	public void mouseMoved() {
		localPlayer.setLookPosition(instance.screen.transform(new Vector(mouseX, mouseY), instance.world));
		instance.updateLocalPlayer();
	}
	@Override
	public void mousePressed() {
		localPlayer.shooting = true;
		instance.updateLocalPlayer();
	}
	@Override
	public void mouseReleased() {
		localPlayer.shooting = false;
		instance.updateLocalPlayer();
	}
	@Override
	public void mouseDragged() {
		localPlayer.setLookPosition(instance.screen.transform(new Vector(mouseX, mouseY), instance.world));
		instance.updateLocalPlayer();
		localPlayer.shoot();
	}
	@Override
	public void mouseWheel(MouseEvent m) {
		Viewport world = instance.world;
		float a = m.getCount();
		float val = Math.min(world.w, world.h);
		if(val + a <= 0)
			return;
		world.zoom(a, instance.screen.aspRatio());
	}
	@Override
	public void keyPressed() {
		if (keyCode == SHIFT)
			localPlayer.setFastMovement(true);
		char k = Character.toLowerCase(key);
		if (k == 'w')
			localPlayer.keys.y = 1;
		if (k == 's')
			localPlayer.keys.y = -1;
		if (k == 'a')
			localPlayer.keys.x = 1;
		if (k == 'd')
			localPlayer.keys.x = -1;
		if (k == '=')
			instance.statistics.incrStatLevel();
		if (k == '-')
			instance.statistics.decrStatLevel();
		if (k == ' ')
			localPlayer.shooting = true;
		instance.updateLocalPlayer();
	}
	@Override
	public void keyReleased() {
		if (keyCode == SHIFT)
			localPlayer.setFastMovement(false);
		char k = Character.toLowerCase(key);
		if (k == 'w' || k == 's')
			localPlayer.keys.y = 0;
		if (k == 'a' || k == 'd')
			localPlayer.keys.x = 0;
		if (k == ' ')
			localPlayer.shooting = false;
		instance.updateLocalPlayer();
	}
	@Override public void onConnect(SocketWrapperThread cw) {}
	@Override public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	@Override public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {}
	@Override public void onDisconnect(SocketWrapperThread cw) {
		exit();
	}
	@Override public void onReceiveData(SocketWrapperThread cw, String data) {}
	@Override public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
	public void exit() {
		System.out.println("Exiting now!");
		System.exit(0);
	}
}

package jeremiahlowe.fightinggame.client;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.math.Vector;
import net.net16.jeremiahlowe.shared.math.Viewport;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class FightingGameClient extends PApplet {
	public static boolean DEBUG_MODE = false;

	public static void main(String[] args) {
		DEBUG_MODE = true;
		main(FightingGameClient.class, args);
	}
	
	public float worldSize = 10;
	
	private GameClientInstance instance;
	private Player player;
	
	@Override
	public void settings() {
		size(500, 500);
		instance = new GameClientInstance(this);
		instance.screen = new Viewport(width, -height, width / 2, height / 2);
		instance.world = new Viewport(worldSize * instance.screen.aspRatio(), worldSize, 0, 0);
		if(!instance.connectToServer("localhost", 1234))
			System.exit(1);
		int serverVersion = instance.getServerVersion();
		if(serverVersion != Meta.VERSION_ID) {
			System.err.println("Server and client have mismatched versions, server is running " + serverVersion + " and client is running " + Meta.VERSION_ID);
			System.exit(1);
		}
		player = instance.getPlayerFromServer();
		if(player == null) {
			System.err.println("Was unable to retrieve player from the server?!");
			System.exit(-1);
		}
		instance.localPlayer = player;
		instance.addDrawable(player);
		instance.addPhysicsObject(player);
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
		player.setLookPosition(instance.screen.transform(new Vector(mouseX, mouseY), instance.world));
		instance.updateLocalPlayer();
	}
	@Override
	public void mousePressed() {
		player.shooting = true;
		instance.updateLocalPlayer();
	}
	@Override
	public void mouseReleased() {
		player.shooting = false;
		instance.updateLocalPlayer();
	}
	@Override
	public void mouseDragged() {
		player.setLookPosition(instance.screen.transform(new Vector(mouseX, mouseY), instance.world));
		instance.updateLocalPlayer();
		player.shoot();
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
			player.setFastMovement(true);
		char k = Character.toLowerCase(key);
		if (k == 'w')
			player.keys.y = 1;
		if (k == 's')
			player.keys.y = -1;
		if (k == 'a')
			player.keys.x = 1;
		if (k == 'd')
			player.keys.x = -1;
		if (k == '=')
			instance.statistics.incrStatLevel();
		if (k == '-')
			instance.statistics.decrStatLevel();
		if (k == ' ')
			player.shooting = true;
		instance.updateLocalPlayer();
	}
	@Override
	public void keyReleased() {
		if (keyCode == SHIFT)
			player.setFastMovement(false);
		char k = Character.toLowerCase(key);
		if (k == 'w' || k == 's')
			player.keys.y = 0;
		if (k == 'a' || k == 'd')
			player.keys.x = 0;
		if (k == ' ')
			player.shooting = false;
		instance.updateLocalPlayer();
	}
}

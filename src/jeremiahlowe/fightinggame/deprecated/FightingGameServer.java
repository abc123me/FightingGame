package jeremiahlowe.fightinggame.deprecated;

import jeremiahlowe.fightinggame.ins.GenericGraphicalInstance;
import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.server.Server;
import jeremiahlowe.fightinggame.util.Viewport;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class FightingGameServer extends PApplet {
	public static boolean DEBUG_MODE = false;

	public static void main(String[] args) {
		DEBUG_MODE = false;
		boolean gui = false;
		for(String arg : args) 
			if(arg != null && arg.equalsIgnoreCase("--show-gui"))
				gui = true;
		
		if(gui) main(FightingGameServer.class, args);
	}
	
	public GraphicalInstance instance;
	public Server server;
	public float worldSize = 10;
	
	@Override
	public void settings() {
		size(500, 500);
		instance = new GenericGraphicalInstance(this);
		instance.screen = new Viewport(width, -height, width / 2, height / 2);
		instance.world = new Viewport(worldSize * instance.screen.aspRatio(), worldSize, 0, 0);
		server = new Server(instance, 1234);
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.serverStop();
			}
		});
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
	public void keyPressed() {
		char k = Character.toLowerCase(key);
		if(k == 'w')
			instance.world.y++;
		if(k == 's')
			instance.world.y--;
		if(k == 'a')
			instance.world.x--;
		if(k == 'd')
			instance.world.x++;
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
}

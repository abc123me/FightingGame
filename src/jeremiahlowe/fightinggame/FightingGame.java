package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.ai.AIFighter;
import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.phys.Player;
import jeremiahlowe.fightinggame.ins.GenericGraphicalInstance;
import net.net16.jeremiahlowe.shared.Color;
import net.net16.jeremiahlowe.shared.math.Vector;
import net.net16.jeremiahlowe.shared.math.Viewport;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class FightingGame extends PApplet {
	public static boolean DEBUG_MODE = false;

	public static void main(String[] args) {
		DEBUG_MODE = false;
		main(FightingGame.class, args);
	}

	Player player;
	float worldSize = 10;
	float timeWarp = 1f;

	public GraphicalInstance instance;

	@Override
	public void settings() {
		size(500, 500);
		instance = new GenericGraphicalInstance(this);
		instance.screen = new Viewport(width, -height, width / 2, height / 2);
		instance.world = new Viewport(worldSize * instance.screen.aspRatio(), worldSize, 0, 0);
		player = new Player(instance);
		player.pos = new Vector(0, 2);
		player.color = Color.YELLOW;
		AIFighter f1 = new AIFighter(instance);
		AIFighter f2 = new AIFighter(instance);
		f2.pos = new Vector(2, 0);
		f1.pos = new Vector(-2, 0);
		f2.attack(f1);
	}
	@Override
	public void setup() {
		if (DEBUG_MODE)
			instance.statistics.level = 9000;
		frameRate(60);
	}
	@Override
	public void draw() {
		instance.world.x = player.pos.x;
		instance.world.y = player.pos.y;
		background(255);
		instance.physicsUpdate((1 / frameRate) * timeWarp);
		instance.drawAll(this);
	}

	@Override
	public void mouseMoved() {
		player.setLookPosition(instance.screen.transform(new Vector(mouseX, mouseY), instance.world));
	}
	@Override
	public void mousePressed() {
		player.shooting = true;
	}
	@Override
	public void mouseReleased() {
		player.shooting = false;
	}
	@Override
	public void mouseDragged() {
		player.setLookPosition(instance.screen.transform(new Vector(mouseX, mouseY), instance.world));
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
	}
}
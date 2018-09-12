package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.math.Vector;

public class PlayerMovementData{
	public Vector keys, look;
	public boolean shooting, speedboostActive;
	public long forUUID;
	
	public PlayerMovementData(Player from) {
		keys = from.keys;
		look = from.look;
		shooting = from.shooting;
		forUUID = from.uuid;
		speedboostActive = from.speedboostActive;
	}

	public void copyTo(Player pl) {
		pl.keys = keys;
		pl.look = look;
		pl.shooting = shooting;
		pl.speedboostActive = speedboostActive;
	}
}
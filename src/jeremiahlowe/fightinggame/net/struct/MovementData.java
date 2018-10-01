package jeremiahlowe.fightinggame.net.struct;

import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.math.Vector;

public class MovementData{
	public Vector keys, look;
	public boolean shooting, speedboost;
	public long forUUID;
	
	public MovementData(Player from) {
		keys = from.keys;
		look = from.look;
		shooting = from.shooting;
		forUUID = from.uuid;
		speedboost = from.hasSpeedBoost();
	}

	public void copyTo(Player pl) {
		pl.keys = keys;
		pl.look = look;
		pl.shooting = shooting;
		pl.setFastMovement(speedboost);
	}
}
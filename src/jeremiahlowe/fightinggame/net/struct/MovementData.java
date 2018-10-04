package jeremiahlowe.fightinggame.net.struct;

import jeremiahlowe.fightinggame.phys.*;
import net.net16.jeremiahlowe.shared.math.*;

public class MovementData{
	public Vector keys, look;
	public boolean shooting, speedboost;
	public long forUUID;
	
	public MovementData(Vector keys, Vector look, boolean shooting, long uuid, boolean speedboost) {
		this.keys = keys;
		this.look = look;
		this.shooting = shooting;
		this.forUUID = uuid;
		this.speedboost = speedboost;
	}
	public MovementData(Player from) {
		this(from.keys, from.look, from.shooting, from.uuid, from.hasSpeedBoost());
	}

	public void copyTo(Player pl) {
		pl.keys = keys;
		pl.look = look;
		pl.shooting = shooting;
		pl.setFastMovement(speedboost);
	}
}
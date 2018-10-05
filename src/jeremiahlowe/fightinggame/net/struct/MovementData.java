package jeremiahlowe.fightinggame.net.struct;

import jeremiahlowe.fightinggame.phys.*;
import net.net16.jeremiahlowe.shared.math.*;

public class MovementData{
	public Vector keys, look, pos, vel;
	public boolean shooting, speedboost;
	public long forUUID;
	
	public MovementData(Vector pos, Vector vel, Vector keys, Vector look, boolean shooting, long uuid, boolean speedboost) {
		this.pos = pos;
		this.vel = vel;
		this.keys = keys;
		this.look = look;
		this.shooting = shooting;
		this.forUUID = uuid;
		this.speedboost = speedboost;
	}
	public MovementData(Player from) {
		this(from.pos, from.vel, from.keys, from.look, from.shooting, from.uuid, from.hasSpeedBoost());
	}

	public void copyTo(Player pl, boolean copyLocationData, boolean copyKeys) {
		if(copyKeys)
			pl.keys = keys;
		pl.look = look;
		pl.shooting = shooting;
		pl.setFastMovement(speedboost);
		if(copyLocationData) {
			if(pos != null) pl.pos = pos;
			if(vel != null) pl.vel = vel;
		}
	}
}
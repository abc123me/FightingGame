package jeremiahlowe.fightinggame.net.struct;

import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.math.Vector;

public class PositionData {
	public final long forUUID;
	public final Vector pos, vel;
	//public final Vector look;
	
	public PositionData(Player pl) {
		this.pos = pl.pos;
		this.vel = pl.vel;
		this.forUUID = pl.uuid;
	}
	
	public void copyTo(Player to) {
		to.pos = pos;
		to.vel = vel;
		//to.look = look;
	}
}

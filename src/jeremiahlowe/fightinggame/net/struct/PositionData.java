package jeremiahlowe.fightinggame.net.struct;

import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.math.Vector;

public class PositionData {
	public final long forUUID;
	public final Vector pos;
	//public final Vector look;
	
	public PositionData(Player pl) {
		this(pl.pos, pl.look, pl.uuid);
	}
	public PositionData(Vector pos, Vector look, long forUUID) {
		this.pos = pos;
		//this.look = look;
		this.forUUID = forUUID;
	}
	
	public void copyTo(Player to) {
		to.pos = pos;
		//to.look = look;
	}
}

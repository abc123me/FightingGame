package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.Instance;
import net.net16.jeremiahlowe.shared.math.Vector;

public class Player extends DamageableFighter {
	public static final int MAX_NAME_LENGTH = 20;
	
	public Vector keys;
	public String name = "Unnamed";
	public float speed, speedBoost;
	public float lookOffset = (float) (1.5f * Math.PI);
	public final long uuid;
	
	private transient float realSpeed;

	public transient boolean ignoreKeys = false;

	public Player(long uuid) {
		super();
		this.uuid = uuid;
		keys = new Vector(0, 0);
		size = 0.5f;
		speed = 7.0f;
		realSpeed = speed;
		speedBoost = 1.75f;
	}

	public void updateControls() {
		if(!ignoreKeys) 
			vel = keys.copy().rotate(heading() + lookOffset).normalize().mult(realSpeed);
	}
	
	@Override
	public void physics(Instance i, double dt) {
		updateControls();
		super.physics(i, dt);
	}
	@Override
	public String toString() {
		return "Player: " + name;
	}

	public void setFastMovement(boolean b) {
		if(b)
			realSpeed = speed * speedBoost;
		else
			realSpeed = speed;
	}
	public boolean hasSpeedBoost() {
		return realSpeed > speed;
	}
}

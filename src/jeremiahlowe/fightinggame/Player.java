package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.util.Math;
import processing.core.PVector;

public class Player extends DamageableFighter {
	public PVector keys;
	public String name = "Unnamed";
	public float speed, speedBoost;
	public float lookOffset = 1.5f * Math.PI;
	
	private float realSpeed;

	public Player(Instance instance) {
		super(instance);
		keys = new PVector(0, 0);
		size = 0.5f;
		speed = 7.0f;
		realSpeed = speed;
		speedBoost = 1.75f;
	}

	public void updateControls() {
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
}
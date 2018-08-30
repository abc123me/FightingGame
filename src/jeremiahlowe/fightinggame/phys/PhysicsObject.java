package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.Instance;
import processing.core.PVector;

public abstract class PhysicsObject {
	public PVector pos, vel;

	protected PhysicsObject() {
		pos = new PVector();
		vel = new PVector();
	}

	public void physics(Instance instance, double dt) {
		pos.x += vel.x * dt;
		pos.y += vel.y * dt;
		for (PhysicsObject p : instance.getPhysicsObjects())
			if (p != null && p.enabled() && p != this)
					if (p.collidesWith(this))
						p.onCollision(this);
	}
	public boolean collidesWith(PhysicsObject p) {
		if (pos.x == p.pos.x && pos.y == p.pos.y)
			return true;
		return false;
	}
	public void onCollision(PhysicsObject with) {

	}
	
	public abstract boolean enabled();
	public abstract void destroy() ;
	
	@Override
	public String toString() {
		return "PhysicsObject";
	}
}


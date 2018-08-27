package jeremiahlowe.fightinggame.ins;

import jeremiahlowe.fightinggame.PhysicsObject;
import jeremiahlowe.fightinggame.util.SafeArrayList;

public abstract class Instance{
	protected SafeArrayList<PhysicsObject> physicsObjects;
	
	public Instance() {
		physicsObjects = new SafeArrayList<PhysicsObject>();
	}

	public void removePhysicsObject(PhysicsObject p) {
		physicsObjects.remove(p);
	}
	public void addPhysicsObject(PhysicsObject p) {
		physicsObjects.add(p);
	}
	public SafeArrayList<PhysicsObject> getPhysicsObjects(){
		return physicsObjects;
	}
	public void physicsUpdate(double dt) {
		physicsObjects.update();
		for (PhysicsObject p : physicsObjects)
			if (p != null && p.enabled())
				p.physics(this, dt);
	}
}
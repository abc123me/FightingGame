package jeremiahlowe.fightinggame.ins;

import jeremiahlowe.fightinggame.phys.PhysicsObject;
import net.net16.jeremiahlowe.shared.QueuedArrayList;

public abstract class Instance{
	protected QueuedArrayList<PhysicsObject> physicsObjects;
	
	public Instance() {
		physicsObjects = new QueuedArrayList<PhysicsObject>();
	}

	public void removePhysicsObject(PhysicsObject p) {
		physicsObjects.remove(p);
	}
	public void addPhysicsObject(PhysicsObject p) {
		physicsObjects.add(p);
	}
	public QueuedArrayList<PhysicsObject> getPhysicsObjects(){
		return physicsObjects;
	}
	public void physicsUpdate(double dt) {
		if(dt <= 0)
			return;
		physicsObjects.update();
		for (PhysicsObject p : physicsObjects)
			if (p != null && p.enabled())
				p.physics(this, dt);
	}
}
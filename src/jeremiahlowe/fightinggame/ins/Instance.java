package jeremiahlowe.fightinggame.ins;

import jeremiahlowe.fightinggame.phys.PhysicsObject;
import net.net16.jeremiahlowe.shared.QueuedArrayList;

public abstract class Instance{
	protected QueuedArrayList<PhysicsObject> physicsObjects;
	
	public Instance() {
		physicsObjects = new QueuedArrayList<PhysicsObject>();
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
	
	public void add(Object p) {
		if(p instanceof PhysicsObject)
			physicsObjects.add((PhysicsObject) p); 
	}
	public void remove(Object p) {
		if(p instanceof PhysicsObject)
			physicsObjects.remove((PhysicsObject) p); 
	}
	public void addAll(Object...objects) {
		for(Object o : objects)
			if(o != null)
				add(o);
	}
	public void removeAll(Object...objects) {
		for(Object o : objects)
			if(o != null)
				remove(o);
	}
}
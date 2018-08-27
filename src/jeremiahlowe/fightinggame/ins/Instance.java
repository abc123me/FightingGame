package jeremiahlowe.fightinggame.ins;

import jeremiahlowe.fightinggame.PhysicsObject;
import jeremiahlowe.fightinggame.ui.IDrawable;
import jeremiahlowe.fightinggame.ui.Statistics;
import jeremiahlowe.fightinggame.util.SafeArrayList;
import jeremiahlowe.fightinggame.util.Viewport;
import processing.core.PApplet;

public class Instance{
	public static final int STATISTICS_DRAW_PRIORITY = 9000;
	public static final int BULLET_DRAW_PRIORITY = 3;
	public static final int FIGHTER_DRAW_PRIORITY = 2;
	
	private SafeArrayList<PhysicsObject> physicsObjects;
	private SafeArrayList<IDrawable> drawables;
	
	public Statistics statistics;
	public Viewport world, screen;
	public PApplet applet;

	public Instance(PApplet applet) {
		this.applet = applet;
		statistics = new Statistics();
		drawables = new SafeArrayList<IDrawable>(IDrawable.PRIORITY_SORT);
		drawables.add(statistics);
		physicsObjects = new SafeArrayList<PhysicsObject>();
	}

	public void removeDrawable(IDrawable d) {
		drawables.remove(d);
	}
	public void removePhysicsObject(PhysicsObject p) {
		physicsObjects.remove(p);
	}
	public void addDrawable(IDrawable d) {
		drawables.add(d);
	}
	public void addPhysicsObject(PhysicsObject p) {
		physicsObjects.add(p);
	}
	public SafeArrayList<PhysicsObject> getPhysicsObjects(){
		return physicsObjects;
	}
	public SafeArrayList<IDrawable> getDrawables(){
		return drawables;
	}
	public void physicsUpdate(double dt) {
		physicsObjects.update();
		for (PhysicsObject p : physicsObjects)
			if (p != null && p.enabled())
				p.physics(this, dt);
	}
	public void drawAll(PApplet p) {
		drawables.update();
		for (IDrawable d : drawables)
			if(d != null && d.enabled())
				d.draw(p, this);
	}
}
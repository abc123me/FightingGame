package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.ui.IDrawable;
import jeremiahlowe.fightinggame.ui.IStatistic.IDrawableStatistic;
import jeremiahlowe.fightinggame.util.Color;
import jeremiahlowe.fightinggame.util.Math;
import jeremiahlowe.fightinggame.util.PGFX;
import jeremiahlowe.fightinggame.util.Timing;
import processing.core.PApplet;
import processing.core.PVector;

public class Bullet extends PhysicsObject implements IDrawable, IDrawableStatistic{
	public float size = 0.1f;
	public float damage = 5;
	public long lifetime = 5000;
	public Color color;

	private final Fighter parent;
	private Timing t;

	private long spawnTime;
	private double deltaTime = 0;
	private boolean enabled;

	public Bullet(Fighter f) {
		super();
		PVector vel = f.getLookVector(f.gunVelocity);
		this.pos = f.pos.copy();
		this.vel = vel.add(f.vel.copy());
		enabled = false;
		parent = f;
		color = f.color;
		t = new Timing();
	}

	public void fire() {
		t.start();
		parent.instance.addPhysicsObject(this);
		if(parent.instance instanceof GraphicalInstance)
			((GraphicalInstance) parent.instance).addDrawable(this);
		enabled = true;
	}
	public long timeLeft() {
		return lifetime - (t.millis() - spawnTime);
	}
	public PVector nextPosition() {
		return PVector.add(pos, vel.copy().mult((float) deltaTime));
	}
	public Fighter getParent() {
		return parent;
	}
	
	@Override
	public void destroy() {
		Instance i = parent.instance;
		i.removePhysicsObject(this);
		if(i instanceof GraphicalInstance)
			((GraphicalInstance) i).removeDrawable(this);
		enabled = false;
	}
	@Override
	public int getDrawPriority() {
		return GraphicalInstance.BULLET_DRAW_PRIORITY;
	}
	@Override
	public void draw(PApplet p, GraphicalInstance i) {
		if (!enabled)
			return;
		p.fill(color.argb());
		PVector pix = i.world.transform(pos, i.screen);
		PVector ss = i.world.transformIgnoreOffset(new PVector(size, size), i.screen);
		p.ellipse(pix.x, pix.y, ss.x, ss.y);
	}
	@Override
	public boolean enabled() {
		return enabled;
	}
	@Override
	public boolean collidesWith(PhysicsObject p) {
		if (p == parent || p instanceof Bullet || p == null)
			return false;
		float dpcol = Math.dist2FromPolygon(p.pos, getColliderVerticies());
		float size2 = size * size;
		if (p instanceof Fighter) {
			float psize = ((Fighter) p).size / 2;
			size2 += psize * psize;
			boolean hit = dpcol <= size2;
			if (hit && p instanceof DamageableFighter)
				((DamageableFighter) p).onHit(this, damage);
			if(hit)
				destroy();
		}
		return dpcol <= size2;
	}
	@Override
	public void physics(Instance i, double dt) {
		deltaTime = dt;
		super.physics(i, dt);
		long aliveTime = t.millis() - spawnTime;
		if (aliveTime > lifetime)
			destroy();
	}
	@Override
	public String toString() {
		return "Bullet from " + parent + " with " + timeLeft() + "ms remaining on lifespan";
	}
	@Override
	public int getLevel() {
		return 3;
	}
	@Override
	public void drawStatistic(PApplet p, GraphicalInstance i) {
		p.fill(new Color(Color.RED, (int)(255 * 0.85f)).argb());
		PGFX.polygon(p, i.world.transformAll(i.screen, getColliderVerticies()));
	}
	
	private PVector[] getColliderVerticies() {
		PVector a = pos.copy(), b = pos.copy();
		PVector npos = nextPosition();
		PVector c = npos.copy(), d = npos.copy();
		a.x -= size / 2;
		b.x += size / 2;
		c.x += size / 2;
		d.x -= size / 2;
		return new PVector[] {a, b, c, d};
	}
}
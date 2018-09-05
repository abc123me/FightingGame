package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.ui.IDrawable;
import jeremiahlowe.fightinggame.ui.IStatistic.IDrawableStatistic;
import jeremiahlowe.fightinggame.util.PGFX;
import net.net16.jeremiahlowe.shared.Color;
import net.net16.jeremiahlowe.shared.Timing;
import net.net16.jeremiahlowe.shared.math.GeneralMath;
import net.net16.jeremiahlowe.shared.math.Vector;
import processing.core.PApplet;

public class Bullet extends PhysicsObject implements IDrawable, IDrawableStatistic{
	public float size = 0.1f;
	public float damage = 5;
	public long lifetime = 5000;
	public Color color;

	private final Fighter parent;
	private Timing t;
	private Instance i;

	private long spawnTime;
	private double deltaTime = 0;
	private boolean enabled;

	public Bullet(Fighter f) {
		super();
		Vector vel = f.getLookVector(f.gunVelocity);
		this.pos = f.pos.copy();
		this.vel = vel.add(f.vel.copy());
		enabled = false;
		parent = f;
		color = f.color;
		t = new Timing();
	}

	public void fire(Instance i) {
		t.start();
		enabled = true;
		this.i = i;
		i.add(this);
	}
	public long timeLeft() {
		return lifetime - (t.millisPassed() - spawnTime);
	}
	public Vector nextPosition() {
		return Vector.add(pos, vel.copy().mult((float) deltaTime));
	}
	public Fighter getParent() {
		return parent;
	}
	
	public int getDrawPriority() {
		return GraphicalInstance.BULLET_DRAW_PRIORITY;
	}
	public void draw(PApplet p, GraphicalInstance i) {
		if (!enabled)
			return;
		p.fill(color.argb());
		Vector pix = i.world.transform(pos, i.screen);
		Vector ss = i.world.transformIgnoreOffset(new Vector(size, size), i.screen);
		p.ellipse(pix.x, pix.y, ss.x, ss.y);
	}
	
	@Override
	public void destroy() {
		enabled = false;
		if(i != null)
			i.remove(this);
	}
	@Override
	public boolean enabled() {
		return enabled;
	}
	@Override
	public boolean collidesWith(PhysicsObject p) {
		if (p == parent || p instanceof Bullet || p == null)
			return false;
		float dpcol = GeneralMath.dist2FromPolygon(p.pos, getColliderVerticies());
		float size2 = size * size;
		if (p instanceof Fighter) {
			float psize = ((Fighter) p).size / 2;
			size2 += psize * psize;
			boolean hit = dpcol <= size2;
			if (hit && p instanceof DamageableFighter)
				((DamageableFighter) p).onHit(i, this, damage);
			if(hit)
				destroy();
		}
		return dpcol <= size2;
	}
	@Override
	public void physics(Instance i, double dt) {
		deltaTime = dt;
		super.physics(i, dt);
		long aliveTime = t.millisPassed() - spawnTime;
		if (aliveTime > lifetime)
			destroy();
	}
	@Override
	public String toString() {
		return "Bullet from " + parent + " with " + timeLeft() + "ms remaining on lifespan";
	}
	
	public int getLevel() {
		return 3;
	}
	public void drawStatistic(PApplet p, GraphicalInstance i) {
		p.fill(new Color(Color.RED, (int)(255 * 0.85f)).argb());
		PGFX.polygon(p, i.world.transformAll(i.screen, getColliderVerticies()));
	}
	
	private Vector[] getColliderVerticies() {
		Vector a = pos.copy(), b = pos.copy();
		Vector npos = nextPosition();
		Vector c = npos.copy(), d = npos.copy();
		a.x -= size / 2;
		b.x += size / 2;
		c.x += size / 2;
		d.x -= size / 2;
		return new Vector[] {a, b, c, d};
	}
}
package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.ui.IDrawable;
import jeremiahlowe.fightinggame.util.Color;
import processing.core.PApplet;
import processing.core.PVector;

public class Fighter extends PhysicsObject implements IDrawable {
	public final Instance instance;

	public float size = 0.5f;
	public float gunVelocity = 10;
	public boolean shooting = false;
	public float shootingSpeed = 0.15f;
	public boolean alive = true;
	public Color color;
	public PVector look;

	private float shootCooldown = 0;

	public Fighter(Instance instance) {
		this(instance, true);
	}
	public Fighter(Instance instance, boolean add) {
		super();
		color = new Color(255, 0, 0);
		look = new PVector();
		size = 1;
		if (add) {
			if(instance instanceof GraphicalInstance)
				((GraphicalInstance) instance).addDrawable(this);
			instance.addPhysicsObject(this);
		}
		this.instance = instance;
	}

	public PVector getLookVector() {
		return look.copy().sub(pos).normalize();
	}
	public PVector getLookVector(float mag) {
		return getLookVector().mult(mag);
	}
	public float heading() {
		return look.copy().sub(pos).heading();
	}
	public void setLookPosition(PVector look) {
		this.look = look;
	}
	public void shoot() {
		if (shootCooldown > 0)
			return;
		shootCooldown = shootingSpeed;
		new Bullet(this).fire();
	}
	public boolean alive() {
		return alive;
	}

	@Override
	public void destroy() {
		alive = false;
		instance.removePhysicsObject(this);
		if(instance instanceof GraphicalInstance)
			((GraphicalInstance) instance).removeDrawable(this);
	}
	@Override
	public int getDrawPriority() {
		return GraphicalInstance.FIGHTER_DRAW_PRIORITY;
	}
	@Override
	public void draw(PApplet p, GraphicalInstance i) {
		p.stroke(p.color(255, 0, 0));
		PVector rpos = i.world.transform(pos, i.screen);
		PVector lpos = i.world.transform(getLookVector(1000).add(pos), i.screen);
		p.line(rpos.x, rpos.y, lpos.x, lpos.y);
		p.fill(color.argb());
		p.stroke(0);
		PVector rsize = i.world.transformIgnoreOffset(new PVector(size, size), i.screen);
		p.ellipse(rpos.x, rpos.y, rsize.x, rsize.y);
	}
	@Override
	public boolean enabled() {
		return true;
	}
	@Override
	public boolean collidesWith(PhysicsObject p) {
		float dx = pos.x - p.pos.x, dy = pos.y - p.pos.y;
		dx *= dx;
		dy *= dy;
		float d2 = dx + dy;
		if (p instanceof Fighter)
			return (d2 <= (((Fighter) p).size + size));
		else
			return (d2 <= size);
	}
	@Override
	public void physics(Instance i, double dt) {
		super.physics(i, dt);
		if (shootCooldown >= 0)
			shootCooldown -= dt;
		if (shooting)
			shoot();
	}
	@Override
	public String toString() {
		return "Fighter.general";
	}
}
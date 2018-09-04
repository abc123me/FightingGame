package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.ui.IDrawable;
import net.net16.jeremiahlowe.shared.Color;
import net.net16.jeremiahlowe.shared.math.Vector;
import processing.core.PApplet;

public class Fighter extends PhysicsObject implements IDrawable {
	public float size = 0.5f;
	public float gunVelocity = 10;
	public boolean shooting = false;
	public float shootingSpeed = 0.15f;
	public boolean alive = true;
	public Color color;
	public Vector look;

	private float shootCooldown = 0;

	public Fighter() {
		super();
		color = new Color(255, 0, 0);
		look = new Vector();
		size = 1;
	}

	public Vector getLookVector() {
		return look.copy().sub(pos).normalize();
	}
	public Vector getLookVector(float mag) {
		return getLookVector().mult(mag);
	}
	public float heading() {
		return look.copy().sub(pos).atan2();
	}
	public void setLookPosition(Vector look) {
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
	}
	
	public int getDrawPriority() {
		return GraphicalInstance.FIGHTER_DRAW_PRIORITY;
	}
	public void draw(PApplet p, GraphicalInstance i) {
		p.stroke(p.color(255, 0, 0));
		Vector rpos = i.world.transform(pos, i.screen);
		Vector lpos = i.world.transform(getLookVector(1000).add(pos), i.screen);
		p.line(rpos.x, rpos.y, lpos.x, lpos.y);
		p.fill(color.argb());
		p.stroke(0);
		Vector rsize = i.world.transformIgnoreOffset(new Vector(size, size), i.screen);
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
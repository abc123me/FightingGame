package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ins.Instance;
import net.net16.jeremiahlowe.shared.math.Vector;
import processing.core.PApplet;

public class DamageableFighter extends Fighter {
	public float health, maxHealth;
	public boolean invincible = false;

	public DamageableFighter() {
		health = 75;
		maxHealth = 100;
	}

	public void onHeal(Instance i, Object from, float by) {
		if (by < 0) {
			onHit(i, from, -by);
			return;
		}
		health += by;
		if (health > maxHealth)
			health = maxHealth;
	}
	public void onHit(Instance i, Object from, float damage) {
		if (invincible)
			return;
		if (damage < 0) {
			onHeal(i, from, -damage);
			return;
		}
		health -= damage;
		if (health <= 0) {
			health = 0;
			onDeath(i);
		}
	}
	public void onDeath(Instance i) {
		alive = false;
		destroy();
		if(i != null)
			i.remove(this);
	}
	
	@Override
	public void draw(PApplet p, GraphicalInstance i) {
		super.draw(p, i);
		p.fill(p.color(0, 255, 0));
		float hpw = size * 1.25f;
		float y = pos.y + size;
		Vector rpos = i.world.transform(new Vector(pos.x - hpw / 2, y), i.screen);
		Vector rsize = i.world.transformIgnoreOffset(new Vector(hpw, hpw / 4), i.screen);
		p.rect(rpos.x, rpos.y, rsize.x, rsize.y);
		if (health == maxHealth)
			return;
		p.fill(p.color(255, 0, 0));
		float w = (health / maxHealth) * hpw;
		rpos = i.world.transform(new Vector(w + pos.x - hpw / 2, y), i.screen);
		rsize = i.world.transformIgnoreOffset(new Vector(hpw - w, hpw / 4), i.screen);
		p.rect(rpos.x, rpos.y, rsize.x, rsize.y);
	}
	@Override
	public String toString() {
		return "Fighter.damageable";
	}
}
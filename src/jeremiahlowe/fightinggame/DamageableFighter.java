package jeremiahlowe.fightinggame;

import jeremiahlowe.fightinggame.ins.Instance;
import processing.core.PApplet;
import processing.core.PVector;

public class DamageableFighter extends Fighter {
	public float health, maxHealth;
	public boolean invincible = false;

	public DamageableFighter(Instance instance) {
		super(instance);
		health = 75;
		maxHealth = 100;
	}

	public void onHeal(Object from, float by) {
		if (by < 0) {
			onHit(from, -by);
			return;
		}
		health += by;
		if (health > maxHealth)
			health = maxHealth;
	}
	public void onHit(Object from, float damage) {
		if (invincible)
			return;
		if (damage < 0) {
			onHeal(from, -damage);
			return;
		}
		health -= damage;
		if (health <= 0) {
			health = 0;
			onDeath();
		}
	}
	public void onDeath() {
		alive = false;
		destroy();
	}
	
	@Override
	public void draw(PApplet p, Instance i) {
		super.draw(p, i);
		p.fill(p.color(0, 255, 0));
		float hpw = size * 1.25f;
		float y = pos.y + size;
		PVector rpos = i.world.transform(new PVector(pos.x - hpw / 2, y), i.screen);
		PVector rsize = i.world.transformIgnoreOffset(new PVector(hpw, hpw / 4), i.screen);
		p.rect(rpos.x, rpos.y, rsize.x, rsize.y);
		if (health == maxHealth)
			return;
		p.fill(p.color(255, 0, 0));
		float w = (health / maxHealth) * hpw;
		rpos = i.world.transform(new PVector(w + pos.x - hpw / 2, y), i.screen);
		rsize = i.world.transformIgnoreOffset(new PVector(hpw - w, hpw / 4), i.screen);
		p.rect(rpos.x, rpos.y, rsize.x, rsize.y);
	}
	@Override
	public String toString() {
		return "Fighter.damageable";
	}
}
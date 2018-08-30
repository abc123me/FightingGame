package jeremiahlowe.fightinggame.ai;

import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.phys.Bullet;
import jeremiahlowe.fightinggame.phys.Fighter;
import processing.core.PVector;

public class AIFighter extends AIFighterBase{
	public AIFighter(Instance instance) {
		super(instance);
		keys = new PVector();
		speedBoost = 1.0f;
		setChaseDistance(2.5f);
	}

	@Override
	public void onHit(Object b, float damage) {
		if (b instanceof Bullet) {
			Fighter f = ((Bullet) b).getParent();
			if (health / maxHealth < 0.75)
				startDodge(f);
			else if (inFOV(f.pos))
				if (canAttack())
					attack(f);
				else
					lookAround();
		}
		super.onHit(b, damage);
	}

	@Override
	public String toString() {
		return "Player.AIFighter";
	}
}

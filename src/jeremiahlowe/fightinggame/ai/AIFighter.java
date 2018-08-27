package jeremiahlowe.fightinggame.ai;

import jeremiahlowe.fightinggame.Bullet;
import jeremiahlowe.fightinggame.Fighter;
import jeremiahlowe.fightinggame.Instance;
import processing.core.PVector;

public class AIFighter extends AIFighterBase{
	public AIFighter(Instance instance) {
		super(instance);
		keys = new PVector();
		speedBoost = 1.0f;
		setChaseDistance(100);
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

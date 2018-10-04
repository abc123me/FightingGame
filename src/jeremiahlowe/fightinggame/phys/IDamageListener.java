package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.Instance;

public interface IDamageListener {
	public void onDeath(Instance i, Object killer, DamageableFighter victim);
	public void onTakeDamage(Instance i, Object from, DamageableFighter to, float amount);
}

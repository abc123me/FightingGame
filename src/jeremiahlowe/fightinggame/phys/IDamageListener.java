package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.Instance;

public interface IDamageListener {
	public void onDeath(Instance i, DamageableFighter from);
	public boolean onTakeDamage(Instance i, Object from, DamageableFighter to, float amount);
}

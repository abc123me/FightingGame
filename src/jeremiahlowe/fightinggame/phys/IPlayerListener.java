package jeremiahlowe.fightinggame.phys;

public interface IPlayerListener {

	public void onShoot(Player p, Bullet b);
	public void onDeath(Player p);
}

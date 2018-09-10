package jeremiahlowe.fightinggame.phys;

import jeremiahlowe.fightinggame.ins.Instance;
import net.net16.jeremiahlowe.shared.QueuedArrayList;
import net.net16.jeremiahlowe.shared.math.Vector;

public class Player extends DamageableFighter {
	public Vector keys;
	public String name = "Unnamed";
	public float speed, speedBoost;
	public float lookOffset = (float) (1.5f * Math.PI);
	public final long uuid;
	
	private transient QueuedArrayList<IPlayerListener> playerListeners;
	private float realSpeed;

	public Player(long uuid) {
		super();
		this.uuid = uuid;
		keys = new Vector(0, 0);
		size = 0.5f;
		speed = 7.0f;
		realSpeed = speed;
		speedBoost = 1.75f;
		playerListeners = new QueuedArrayList<IPlayerListener>();
	}

	public void updateControls() {
		vel = keys.copy().rotate(heading() + lookOffset).normalize().mult(realSpeed);
	}

	private void listenersCallOnShoot(Bullet b) {
		if(playerListeners == null)
			playerListeners = new QueuedArrayList<IPlayerListener>();
		else
			playerListeners.update();
		for(IPlayerListener ipl : playerListeners)
			if(ipl != null)
				ipl.onShoot(this, b);
	}
	private void listenersCallOnDeath(Player player) {
		if(playerListeners == null)
			playerListeners = new QueuedArrayList<IPlayerListener>();
		else
			playerListeners.update();
		for(IPlayerListener ipl : playerListeners)
			if(ipl != null)
				ipl.onDeath(this);
	}
	
	@Override
	public Bullet shoot() {
		Bullet b = super.shoot();
		if(b != null)
			listenersCallOnShoot(b);
		return b;
	}
	@Override
	public void onDeath(Instance i) {
		super.onDeath(i);
		listenersCallOnDeath(this);
	}
	@Override
	public void physics(Instance i, double dt) {
		updateControls();
		super.physics(i, dt);
	}
	@Override
	public String toString() {
		return "Player: " + name;
	}

	public void setFastMovement(boolean b) {
		if(b)
			realSpeed = speed * speedBoost;
		else
			realSpeed = speed;
	}
	public void addPlayerListener(IPlayerListener ipl) {
		playerListeners.add(ipl);
	}
	public void removePlayerListener(IPlayerListener ipl) {
		playerListeners.remove(ipl);
	}
}

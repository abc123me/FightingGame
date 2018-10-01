package jeremiahlowe.fightinggame.net.struct;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.phys.DamageableFighter;
import jeremiahlowe.fightinggame.phys.Player;

public class HealthData {
	private final transient Gson gson = new Gson();
	
	public final float health, maxHealth;
	public final long forUUID;
	
	public HealthData(Player p) {
		health = p.health;
		maxHealth = p.maxHealth;
		forUUID = p.uuid;
	}
	public HealthData(DamageableFighter df, long uuid) {
		forUUID = uuid;
		health = df.health;
		maxHealth = df.maxHealth;
	}
	
	public void copyTo(DamageableFighter f) {
		f.health = health;
		f.maxHealth = maxHealth;
	}
	public Packet toPacket() {
		return Packet.createUpdate(EPacketIdentity.PLAYER_HEALTH, gson.toJson(this));
	}
	
	@Override
	public String toString() {
		return "HealthData{" + forUUID + " health " + health + "/" + maxHealth + "}";
	}
}
